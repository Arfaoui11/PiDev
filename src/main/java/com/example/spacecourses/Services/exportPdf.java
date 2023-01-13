package com.example.spacecourses.Services;

import com.example.spacecourses.Entity.Formation;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Stream;

@Service
public class exportPdf {

    public static ByteArrayInputStream FormationPDFReport(List<Formation> formations) throws IOException {
        Document document=new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document,out);
            document.open();
            //add text to pdf file
            Font font= FontFactory.getFont(FontFactory.COURIER,12,BaseColor.LIGHT_GRAY);
            Paragraph para = new Paragraph("Formation List ",font);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);
            PdfPTable table=new PdfPTable(9);
            //make the columns
            Stream.of("Id","Title","domain","Frais","Niveau","startDay","EndDay","nbrHeurs","NbrMax").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell();
                Font headfont= FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                header.setBackgroundColor(BaseColor.YELLOW);
                header.setBorderWidth(12);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setBorderWidth(1);
                header.setPhrase(new Phrase(headerTitle,headfont));
                table.addCell(header);


            });

            for (Formation f : formations){

                //Id university
                PdfPCell idCell = new PdfPCell(new Phrase((f.getIdFormation().toString())));
                idCell.setPaddingLeft(1);
                idCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                idCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(idCell);
                //Name university
                PdfPCell nameCell = new PdfPCell(new Phrase(f.getTitle()));
                nameCell.setPaddingLeft(1);
                nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(nameCell);

                PdfPCell countryCell = new PdfPCell(new Phrase(f.getDomain().toString()));
                countryCell.setPaddingLeft(1);
                countryCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                countryCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(countryCell);

                PdfPCell areaCell = new PdfPCell(new Phrase(String.valueOf(f.getFrais())));
                areaCell.setPaddingLeft(1);
                areaCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                areaCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(areaCell);

                PdfPCell specialtyCell = new PdfPCell(new Phrase(String.valueOf(f.getNiveau())));
                specialtyCell.setPaddingLeft(1);
                specialtyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                specialtyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(specialtyCell);

                PdfPCell start = new PdfPCell(new Phrase(f.getStart().toString()));
                start.setPaddingLeft(1);
                start.setVerticalAlignment(Element.ALIGN_MIDDLE);
                start.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(start);

                PdfPCell end = new PdfPCell(new Phrase(f.getEnd().toString()));
                end.setPaddingLeft(1);
                end.setVerticalAlignment(Element.ALIGN_MIDDLE);
                end.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(end);



                PdfPCell CapacityReceptionCell = new PdfPCell(new Phrase(String.valueOf(f.getNbrHeures())));
                CapacityReceptionCell.setPaddingLeft(1);
                CapacityReceptionCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                CapacityReceptionCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(CapacityReceptionCell);

                PdfPCell max = new PdfPCell(new Phrase(String.valueOf(f.getNbrMaxParticipant())));
                max.setPaddingLeft(1);
                max.setVerticalAlignment(Element.ALIGN_MIDDLE);
                max.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(max);


            }
            // Creating an ImageData object
            String url = "./src/main/resources/static/img/QRCode.png";
            Image image = Image.getInstance(url);
            document.add(image);


            document.add(table);
            document.close();

        } catch ( DocumentException | MalformedURLException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }
}
