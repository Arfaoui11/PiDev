package com.example.spacecourses.Repository;

import com.example.spacecourses.Entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IUserRepo extends CrudRepository<User,Integer> {

    @Query(value= "select f.title , count(a.id) from Formation f join f.apprenant a group by f")
    List<Object[]> getNbrApprenantByFormation();


    @Query(value="select f.apprenant from  Formation f  where f.idFormation = :id")
    List<User> getRevenueByFormation(@Param("id") Integer idFormation);


    @Query(value = "select f.apprenant from  Formation f  where f.idFormation = :id")
    List<User> getApprenantByFormation(@Param("id") Integer idF );


    @Query(value= "select SUM(f.nbrHeures*f.formateur.tarifHoraire) from Formation f where f.formateur.id=:id and f.start>=:dateD and f.end<=:dateF")
    Integer getFormateurRemunerationByDate(@Param("id") Integer idFormateur, @Param("dateD") Date dateDebut, @Param("dateF") Date dateFin);


    @Query(value="select count(a.id) from Formation f join f.apprenant a where f.title=:titre")
    Integer getNbrApprenantByFormation(@Param("titre") String titre );


    @Query(value="select f from User f where f.role=0")
    List<User> getFormateur();



    @Query(value="select f from User f where f.role=1")
    List<User> getApprenant();



    @Query(value = "select f.formateur from Formation f  where f.formateur.tarifHoraire =(select Max(u.tarifHoraire) from User u where u.role=0)")
    User FormateurwithMaxHo();



    @Query(value = "select Max(r.totalCorrect) from User u join u.formationA f join f.quizzes q join q.results r  where u.role=1")
    Integer MaxScoreInFormation();


    @Query(value = "select r.sUser from Formation f join f.quizzes q join q.results r where" +
            " r.totalCorrect=(select Max(r.totalCorrect) from User u join u.formationA f join f.quizzes q join q.results r " +
            " where u.role=1) and f.idFormation=:id")
    User ApprenentwithMaxScoreInFormation(@Param("id") Integer id);

    @Query(value = "select r.sUser,SUM (r.totalCorrect) from Result r join r.quiz q join q.formation f where f.idFormation=:id group by r.sUser order by SUM (r.totalCorrect) desc")
    List<Object> getApprenantWithScoreQuiz(@Param("id") Integer id);










}
