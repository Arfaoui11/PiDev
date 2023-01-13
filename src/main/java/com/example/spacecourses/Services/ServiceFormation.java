package com.example.spacecourses.Services;


import com.example.spacecourses.Entity.*;
import com.example.spacecourses.QrCode.QRCodeGenerator;
import com.example.spacecourses.Repository.*;
import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;


@Slf4j
@Service
public class ServiceFormation implements IServiceFormation{

    @Autowired
    private IUserRepo iUserRepo;

    @Autowired
    private IFormationRepo iFormationRepo;
    @Autowired
    private IResultRepo iResultRepo;

    @Autowired
    private EmailSenderService emailSenderService;

    private static final String QR_CODE_IMAGE_PATH = "./src/main/resources/static/img/QRCode.png";





    @Override
    public void ajouterFormateur(User formateur) {
        iUserRepo.save(formateur);
    }

    @Override
    public void addFormation(Formation formation) {
        iFormationRepo.save(formation);
    }

    @Override
    public void updateFormation(Formation formation, Integer idFormateur) {
        Formation f = iFormationRepo.findById(idFormateur).orElse(null);

        f.setTitle(formation.getTitle());
        f.setDomain(formation.getDomain());
        f.setStart(formation.getStart());
        f.setEnd(formation.getEnd());
        f.setFrais(formation.getFrais());
        f.setNbrHeures(formation.getNbrHeures());
        f.setNbrMaxParticipant(formation.getNbrMaxParticipant());

        //  formation.setFormateur(formateur);
        iFormationRepo.save(f);
    }

    @Override
    public void deleteFormation(Integer idFormation) {
        Formation f = iFormationRepo.findById(idFormation).orElse(null);
        iFormationRepo.delete(f);
    }


    @Override
    public List<Formation> afficherFormation() {
        List<Formation> f =   (List<Formation>)iFormationRepo.findAll();
            return  f;
    }



    @Override
    public List<User> afficherFormateur() {

       return iUserRepo.getFormateur();
    }

    @Override
    public List<User> afficherApprenant() {
        return iUserRepo.getApprenant();
    }

    @Override
    public User FormateurwithMaxHo() {
        return iUserRepo.FormateurwithMaxHo();
    }


    @Override
   // @Scheduled(cron = "0 0/5 * * * *")
    public User getFormateurRemunerationMaxSalaire() {
        int max = 0 ;

        TreeMap<Integer, String> map = new TreeMap<Integer, String>();

        User u = new User();

        LocalDate currentdDate1 =  LocalDate.now();

        ZoneId defaultZoneId = ZoneId.systemDefault();

        Date dd =Date.from(currentdDate1.minusDays(15).atStartOfDay(defaultZoneId).toInstant());
        Date df =Date.from(currentdDate1.plusDays(15).atStartOfDay(defaultZoneId).toInstant());

        for (Formation f: this.iFormationRepo.findAll()) {
            if (f.getStart().after(dd) && f.getEnd().before(df) )
            {
                map.put(this.iFormationRepo.getFormateurRemunerationByDate(f.getFormateur().getId(),dd,df),f.getFormateur().getId().toString());
                if(this.iFormationRepo.getFormateurRemunerationByDate(f.getFormateur().getId(),dd,df) > max)
                {
                    max =  this.iFormationRepo.getFormateurRemunerationByDate(f.getFormateur().getId(),dd,df);
                }
            }

        }

        log.info(" liste"+ map);
        log.info(" Max Salaire " + max);

        for (Formation f: this.iFormationRepo.findAll())
        {
            if (f.getStart().after(dd) && f.getEnd().before(df) )
            {
        if(this.iFormationRepo.getFormateurRemunerationByDate(f.getFormateur().getId(),dd,df) == max)
        {
            u = this.iUserRepo.findById(f.getFormateur().getId()).orElse(null);
        }
        }
        }
        u.setTarifHoraire(u.getTarifHoraire()+10);
        iUserRepo.save(u);
        this.emailSenderService.sendEmail(u.getEmail(),"we have max houre of travel ","we have max houre of travel we elevate salarie "+u.getNom()+"--"+u.getPrenom()+" : ");
      return u;
    }


    public TreeMap<Integer, User> getFormateurRemunerationMaxSalaireTrie() {

        TreeMap<Integer, User> map = new TreeMap<>();



        LocalDate currentdDate1 =  LocalDate.now();

        ZoneId defaultZoneId = ZoneId.systemDefault();

        Date dd =Date.from(currentdDate1.minusDays(15).atStartOfDay(defaultZoneId).toInstant());
        Date df =Date.from(currentdDate1.plusDays(15).atStartOfDay(defaultZoneId).toInstant());

        for (Formation f: this.iFormationRepo.findAll()) {
            if (f.getStart().after(dd) && f.getEnd().before(df) )
            {
                map.put(this.iFormationRepo.getFormateurRemunerationByDate(f.getFormateur().getId(),dd,df),f.getFormateur());

            }

        }
      //  List<Map.Entry<Integer, User>> singleList = map.entrySet().stream().collect(Collectors.toList());
        return map;
    }

    @Override
    public List<Object> getFormateurRemunerationByDateTrie() {
        LocalDate currentdDate1 =  LocalDate.now();

      //  Formation f = iFormationRepo.findById(idFormateur).orElse(null);

        ZoneId defaultZoneId = ZoneId.systemDefault();

        Date dd =Date.from(currentdDate1.minusDays(15).atStartOfDay(defaultZoneId).toInstant());
        Date df =Date.from(currentdDate1.plusDays(15).atStartOfDay(defaultZoneId).toInstant());

        return this.iFormationRepo.getFormateurRemunerationByDateTrie(dd,df);
    }

    @Override
    //@Scheduled(cron = "0 0/1 * * * *")
    public void CertifactionStudents() {

        boolean status = false;
        Formation fr = new Formation();
        User user = new User();

        try {


            for (Formation f : iFormationRepo.findAll())
            {
                for (User u : iUserRepo.getApprenantByFormation(f.getIdFormation()))
                {
                    if(iResultRepo.getScore(u.getId()) >= 200 && iResultRepo.getScore(u.getId()) <=250 && iResultRepo.getNbrQuiz(u.getId()) == 5 )
                    {


                        for (Result r : iResultRepo.getResultByIdUAndAndIdF(u.getId(),f.getIdFormation()))
                        {

                            if (!r.isStatus())
                            {
                                fr=f;
                                user=u;
                                r.setStatus(true);
                                iResultRepo.save(r);
                            }

                        }




                    }

                }

            }

            if (status==true)
            {
                QRCodeGenerator.generateQRCodeImage(fr.getDomain().toString(),150,150,QR_CODE_IMAGE_PATH);
                this.emailSenderService.sendEmail(user.getEmail()," Congratulations Mr's : "+user.getNom()+" "+user.getPrenom()+" you have finished your Courses  " ," Certification At : "+ new Date()+"  in Courses of Domain "+fr.getDomain()+" "+" And Niveau : " +fr.getNiveau() +" .");

            }





        } catch (WriterException | IOException e) {

            e.printStackTrace();
        }



    }


    /*
        @Override
        public List<Map.Entry<Integer, User>> getFormateurRemunerationMaxSalaireTrie() {

            TreeMap<Integer, User> map = new TreeMap<>();



            LocalDate currentdDate1 =  LocalDate.now();

            ZoneId defaultZoneId = ZoneId.systemDefault();

            Date dd =Date.from(currentdDate1.minusDays(15).atStartOfDay(defaultZoneId).toInstant());
            Date df =Date.from(currentdDate1.plusDays(15).atStartOfDay(defaultZoneId).toInstant());

            for (Formation f: this.iFormationRepo.findAll()) {
                if (f.getStart().after(dd) && f.getEnd().before(df) )
                {
                    map.put(this.iFormationRepo.getFormateurRemunerationByDate(f.getFormateur().getId(),dd,df),f.getFormateur());

                }

            }

            List<Map.Entry<Integer, User>> singleList = map.entrySet().stream().collect(Collectors.toList());




            return singleList;
        }


     */
    @Override
    public void ajouterApprenant(User apprenant) {
            iUserRepo.save(apprenant);
    }

    //@Transactional
    @Override
    public void ajouterEtAffecterFormationAFormateur(Formation formation, Integer idFormateur) {

        User formateur = iUserRepo.findById(idFormateur).orElse(null);

        LocalDate currentdDate1 =  LocalDate.now();

        ZoneId defaultZoneId = ZoneId.systemDefault();

        Date dd =Date.from(currentdDate1.minusMonths(3).atStartOfDay(defaultZoneId).toInstant());
        Date df =Date.from(currentdDate1.plusMonths(3).atStartOfDay(defaultZoneId).toInstant());

            if (this.iFormationRepo.nbrCoursesParFormateur(idFormateur,dd,df) <2 && formateur.getRole() == Role.FORMATEUR)
            {
                formation.setLikes(0);
                formation.setDislikes(0);
                formation.setFormateur(formateur);
                iFormationRepo.save(formation);
            }else
            {
                this.emailSenderService.sendEmail(formateur.getEmail(),"we don't have acces to have two coursus in same semester " ,"we have 2 (MAX formation in this semester) NAME : "+formateur.getNom() +" "+formateur.getPrenom() +" .");
                log.info("we have 2 (MAX formation in this Semester ");
            }

    }


    public Formation getFile(Integer fileId) throws FileNotFoundException {
        return iFormationRepo.findById(fileId).orElseThrow(() -> new FileNotFoundException("File not found with id " + fileId));
    }

    @Override
   // @Scheduled(cron = "*/30 * * * * *")
    public void affecterApprenantFormationWithMax(Integer idApprenant, Integer idFormation) {

        Formation formation = iFormationRepo.findById(idFormation).orElse(null);

        User apprenant = iUserRepo.findById(idApprenant).orElse(null);

        LocalDate currentdDate1 =  LocalDate.now();

        ZoneId defaultZoneId = ZoneId.systemDefault();

        Date dd =Date.from(currentdDate1.minusMonths(3).atStartOfDay(defaultZoneId).toInstant());
        Date df =Date.from(currentdDate1.plusMonths(3).atStartOfDay(defaultZoneId).toInstant());


        if (iFormationRepo.getNbrApprenantByFormationId(idFormation) < formation.getNbrMaxParticipant() && apprenant.getRole() == Role.APPRENANT)
        {

            if(iFormationRepo.getNbrFormationByApprenant(idApprenant, formation.getDomain(), dd, df) <2)
            {
                formation.getApprenant().add(apprenant);
                iFormationRepo.save(formation);
            }else
            {
                this.emailSenderService.sendEmail(apprenant.getEmail(),"we don't have acces to add two coursus in same domain " ,"we have 2 (MAX formation in this domain) NAME : "+apprenant.getNom() +" "+apprenant.getPrenom() +" .");
                log.info("this apprenant we have 2 (MAX formation in this domain ");
            }
        }else
        {
            this.emailSenderService.sendEmail(apprenant.getEmail(),"Learner list complete  " ," We have in this courses " + formation.getNbrMaxParticipant() + " number of learner Maximum" + apprenant.getNom() + " - "+apprenant.getPrenom() + "  .");
            log.info(" Learner list complete Max learner " + formation.getNbrMaxParticipant());
        }



    }
/*
    @EventListener(ApplicationReadyEvent.class)
    public void sendMail()
    {
        emailSenderService.sendEmail("mahdijr2015@gmail.com","we don't add two coursus in same domain " ,"this apprenant we have 2 (MAX formation in this domain");
    }


 */
    ///////////////  Affectation 3adiya  ////////////////////
    @Override
    public void affecterApprenantFormation(Integer idApprenant, Integer idFormation) {
        User apprenant = iUserRepo.findById(idApprenant).orElse(null);
        Formation formation = iFormationRepo.findById(idFormation).orElse(null);

        formation.getApprenant().add(apprenant);
        iFormationRepo.save(formation);
    }



    @Override
    public Integer nbrCoursesParFormateur(Integer idF,Date dateDebut, Date dateFin) {
        return this.iFormationRepo.nbrCoursesParFormateur(idF, dateDebut, dateFin);
    }

    @Override
    public Integer getNbrApprenantByFormation(String title) {
        return  iFormationRepo.getNbrApprenantByFormation(title);
    }


    @Override
   // @Scheduled(cron = "*/30 * * * * *")
    public void getNbrApprenantByFormationn() {

        log.info("La formation : Spring contient : " +iFormationRepo.getNbrApprenantByFormation("Spring") + " Apprenant ");
        log.info("La formation : Devops contient : " +iFormationRepo.getNbrApprenantByFormation("DevOps") + " Apprenant ");

    }

    @Override
    public Integer getNbrFormationByApprenant(Integer idApp , Domain domain, Date dateDebut, Date dateFin) {
        List<Formation> f =   (List<Formation>)iFormationRepo.findAll();
       /* User apprenants = iUserRepo.findById(idApp).orElse(null);
        assert apprenants != null;
        List<Formation> fr = apprenants.getFormationA().stream().toList();*/
        return (int)f.stream().filter(app -> app.getStart().after(dateDebut)  && app.getEnd().before(dateFin) && app.getDomain().equals(domain) && app.getApprenant().stream().anyMatch(a -> Objects.equals(a.getId(), idApp))).count();


        // return iFormationRepo.getNbrFormationByApprenant(idApp,domain, dateDebut, dateFin);
    }

    @Override
    public List<Object[]> getNbrApprenantByFormation() {

        return iUserRepo.getNbrApprenantByFormation();
    }

    @Override
    public List<User> getApprenantByFormation(Integer idF) {

        Formation f =   iFormationRepo.findById(idF).orElse(null);

        assert f != null;
        return   f.getApprenant().stream().toList();
        //return iUserRepo.getApprenantByFormation(idF);
    }

    @Override
    public Integer getFormateurRemunerationByDate(Integer idFormateur, Date dateDebut, Date dateFin) {

        return iFormationRepo.getFormateurRemunerationByDate(idFormateur, dateDebut, dateFin);

    }


    @Override
    public Integer getRevenueByFormation(Integer idFormation) {
        Formation f = iFormationRepo.findById(idFormation).orElse(null);

        Integer revenue =  (f.getFrais()*iUserRepo.getRevenueByFormation(idFormation).size());
        return  revenue;
    }

    @Override
    public void likeFormation(Integer idF) {
        Formation formation = iFormationRepo.findById(idF).orElse(null);

        formation.setLikes(formation.getLikes()+1);
        iFormationRepo.save(formation);


    }

    @Override
    public void dislikeFormation(Integer idF) {
        Formation formation = iFormationRepo.findById(idF).orElse(null);

        formation.setDislikes(formation.getDislikes()+1);
        iFormationRepo.save(formation);

    }




}
