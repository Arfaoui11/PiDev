package com.example.spacecourses.Services;


import com.example.spacecourses.Entity.*;

import com.example.spacecourses.Repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
@Slf4j
@Service
public class ServiceQuiz implements IServicesQuiz {

    @Autowired
    private IUserRepo iUserRepo;
    @Autowired
    private IFormationRepo iFormationRepo;
    @Autowired
    private IQuestionRepo iQuestionRepo;
    @Autowired
    private IQuizRepo iQuizRepo;
    @Autowired
    private IResultRepo iResultRepo;
    @Autowired
    Question question;
    @Autowired
    QuestionForm qForm;
    @Autowired
    Result result;




    @Override
    public void addQuiz(Quiz quiz, Integer idF) {
        Formation formation = this.iFormationRepo.findById(idF).orElse(null);
        quiz.setFormation(formation);
        iQuizRepo.save(quiz);
    }

    @Override
    public void addQuestionAndAsigntoQuiz(Question question, Integer idQuiz) {
    Quiz quiz = iQuizRepo.findById(idQuiz).orElse(null);

    question.setQuiz(quiz);

    iQuestionRepo.save(question);
    }







    @Override
    public List<Question> getQuizQuestion() {
        List<Question> allQues =  iQuizRepo.getQuizQuestion();
        List<Question> qList = new ArrayList<>();

        Random random = new Random();

        for(int i=0; i<4; i++) {
            int rand = random.nextInt(allQues.size());
            qList.add(allQues.get(rand));
            allQues.remove(rand);
        }

        return qList;

    }



    public List<Question> getQuestions() {
        List<Question> allQues = (List<Question>) iQuestionRepo.findAll();
        List<Question> qList = new ArrayList<Question>();

        Random random = new Random();

        for(int i=0; i<4; i++) {
            int rand = random.nextInt(allQues.size());
            qList.add(allQues.get(rand));
            allQues.remove(rand);
        }

        return qList;
    }

    public int getResult(QuestionForm qForm) {
        int correct = 0;

        for(Question q: qForm.getQuestions())
            if(q.getAns() == q.getChose())
                correct++;

        return correct;
    }

    public Integer saveScore(Result result, Integer idUser, Integer idQuiz ) {
        Result saveResult = new Result();

        User user = this.iUserRepo.findById(idUser).orElse(null);
        Quiz quiz = this.iQuizRepo.findById(idQuiz).orElse(null);
        if (iResultRepo.findUserQuiz(idUser,idQuiz) == 0)
        {
            saveResult.setSUser(user);
            saveResult.setQuiz(quiz);

            saveResult.setUsername(result.getUsername());
            saveResult.setTotalCorrect(result.getTotalCorrect());
            saveResult.setCorrectAnswer(result.getCorrectAnswer());
            saveResult.setInCorrectAnswer(result.getInCorrectAnswer());
            iResultRepo.save(saveResult);
            return 1;

        }else{
            log.info("This user is tested with this quiz");
            return 0;
        }

    }

    @Override
    public User ApprenentwithMaxScoreInFormation(Integer id) {
        return this.iUserRepo.ApprenentwithMaxScoreInFormation(id);
    }

    @Override
    public Object ApprenentwithMaxScore(Integer id) {
        Formation f = iFormationRepo.findById(id).orElse(null);

        return  iUserRepo.getApprenantWithScoreQuiz(id).get(0);


    }

    @Override
    public Integer MaxScoreInFormation() {
        return this.iUserRepo.MaxScoreInFormation();
    }

    @Override
    public List<Object> getApprenantWithScoreQuiz(Integer id) {
        return this.iUserRepo.getApprenantWithScoreQuiz(id);
    }
    @Override
     public List<Result> getTopScore() {
        List<Result> sList = (List<Result>) iResultRepo.findAll();
       // List<Result> sList = (List<Result>) iResultRepo.findAll(Sort.by(Sort.Direction.DESC, "totalCorrect"));

        return sList.stream()
                .sorted(Comparator.comparing(Result::getTotalCorrect).reversed()).toList();
   }

    @Override
    public Double getScore(Integer idU) {
        List<Result> sList = (List<Result>) iResultRepo.findAll();
        return sList.stream().filter(u -> u.getSUser().getId() == idU).mapToDouble(a -> a.getTotalCorrect())
                .sum();
        //return iResultRepo.getScore(idU);
    }


}
