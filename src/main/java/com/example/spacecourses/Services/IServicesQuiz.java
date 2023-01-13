package com.example.spacecourses.Services;


import com.example.spacecourses.Entity.*;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface IServicesQuiz {


    void addQuiz(Quiz quiz, Integer idF);
    void addQuestionAndAsigntoQuiz(Question question, Integer idQuiz);
   // void addAnswerAndAsigntoQuestion(Answer answer,Integer idQuestion,Integer idQuiz);

    List<Question> getQuizQuestion();
    List<Question> getQuestions();
    int getResult(QuestionForm qForm);
    Integer saveScore(Result result, Integer idUser, Integer idQuiz);
    User ApprenentwithMaxScoreInFormation(Integer id);

    Object ApprenentwithMaxScore(@Param("id") Integer id);
    Integer MaxScoreInFormation();

    List<Object> getApprenantWithScoreQuiz(@Param("id") Integer id);

    List<Result> getTopScore();

    Integer getScore( Integer idU);




}
