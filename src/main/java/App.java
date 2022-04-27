import com.fasterxml.uuid.Generators;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

import static spark.Spark.*;
public class App {
    static ArrayList<Car> dataArray = new ArrayList<>();
    public static void main(String[] args) {
        staticFiles.location("/public");
      get("/", (req, res) -> mainFunction(req, res));
       get("/dane", (req, res) -> daneFunction(req, res));
        get("/admin", (req, res) -> adminFunction(req, res));
        post("/add", (req,res) -> addFunction(req,res));
        post("/post", (req,res) -> postFunction(req,res));
        post("/delete", (req,res) -> deleteFunction(req,res));
        post("/update", (req,res) -> updateFunction(req,res));
        post("/generate" , (req,res) -> generateInvoice(req,res));
        get("/download/" , (req,res) -> downloadInvoice(req,res));


    };
    static String mainFunction(Request req, Response res){
        System.out.println(req.body());
        Gson gson = new Gson();
//        Car myClass = gson.fromJson(req.body(), Car.class);
        return req.body();
    };
    static String daneFunction(Request req, Response res){

        res.redirect("/dane.html");
//        Car myClass = gson.fromJson(req.body(), Car.class);
        return "";
    };
    static String adminFunction(Request req, Response res){

        res.redirect("/admin.html");
//        Car myClass = gson.fromJson(req.body(), Car.class);
        return "";
    };
    static String addFunction(Request req, Response res){
        System.out.println(req.body());
//        String name =  req.queryParams("name");
//        String rok = req.queryParams("country");
//        String color =  req.queryParams("broken");
//        String amount =  req.queryParams("amount");
        UUID uuid = Generators.randomBasedGenerator().generate();
        Gson gson = new Gson();
        Car car = gson.fromJson(req.body(), Car.class);
        car.setUuid(uuid.toString());
        System.out.println(car.toString());
        String model = gson.fromJson(req.body(), Car.class).getModel();
        System.out.println(model);
        dataArray.add(car);
        return gson.toJson(car);
    }
    static String postFunction(Request req, Response res){
        Gson gson = new Gson();
        System.out.println(dataArray);
        return gson.toJson(dataArray);
    }
    static String deleteFunction(Request req, Response res){
        System.out.println("========================");
        System.out.println(req.body());
        System.out.println(dataArray);
        for(Car car : dataArray) {
            if(car.getUUID().equals(req.body())){
                //found it!
                System.out.println(car);
                System.out.println(car.getModel());
                dataArray.remove(car);
            }
        }
        System.out.println(dataArray);
        return req.body();
    }
    static String updateFunction( Request req, Response res){
        System.out.println("========================");
        Gson gson = new Gson();
        UpdateData newCar = gson.fromJson(req.body(), UpdateData.class);



        int counter = 0;
        for(Car car : dataArray) {
            System.out.println(car.getUUID());
            if(car.getUUID().equals(newCar.getUUID())){
                //found it!
                System.out.println(car.getUUID());
                System.out.println(newCar.getUUID());
//                System.out.println(car);
//                System.out.println(car.getModel());
//                System.out.println(newCar);
//                System.out.println(newCar.getUUID());
                dataArray.get(counter).model = newCar.model;
                dataArray.get(counter).rok = newCar.rok;

//                dataArray.set(car, new Car())
            }
            counter++;
        }
//        System.out.println(dataArray);
        return req.body();
    };
    static String generateInvoice (Request req, Response res){
        Gson gson = new Gson();

        Car carInfo = gson.fromJson(req.body(), Car.class);

        System.out.println("========================");
        Document document = new Document(); // dokument pdf
        String path = String.format("./invoices/plik_%s.pdf", carInfo.getUUID()); // lokalizacja zapisu
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Paragraph p = new Paragraph(String.format("Faktura dla %s", carInfo.getUUID()), font);
        try {
            document.add(p);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Font heading = FontFactory.getFont(FontFactory.COURIER, 24, BaseColor.BLACK);
        Chunk chunk = new Chunk(carInfo.getModel(), heading); // akapit

        try {
            document.add(chunk);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        Font smallRed = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.RED);
        Paragraph color = new Paragraph(String.format("color: %s", carInfo.getColor()), smallRed); // akapit

        try {
            document.add(color);
        } catch (DocumentException e) {
            e.printStackTrace();
        }


        Font smallBlack = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);

        Paragraph rok = new Paragraph(String.format("Rok: %s", carInfo.getRok()), smallBlack); // akapit

        try {
            document.add(rok);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        for ( Airbag poducha: carInfo.poduszkiArr) {
            System.out.println(poducha);
            Paragraph poduszka = new Paragraph(String.format("poduszka: %1$s - %2$s ", poducha.poduszka, poducha.ma), smallBlack);
            try {
                document.add(poduszka);
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }

//        Image img = null;
//        try {
//            img = Image.getInstance("path_to_image.jpg");
//        } catch (BadElementException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            document.add(img);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        Paragraph poduszka = new Paragraph(String.format("color %s", carInfo.poduszkiArr), smallRed); // akapit




        document.close();
        return req.body();
    };
    static String downloadInvoice (Request req, Response res){

        System.out.println("========================");
        System.out.println("robi download");
//        Document document = new Document(); // dokument pdf
//        String path = "./invoices/plik2.pdf"; // lokalizacja zapisu
//        try {
//            PdfWriter.getInstance(document, new FileOutputStream(path));
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//
//        document.open();
//        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
//        Chunk chunk = new Chunk("tekst", font); // akapit
//
//        try {
//            document.add(chunk);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        document.close();
        res.type("application/octet-stream"); //
        res.header("Content-Disposition", "attachment; filename=" + String.format("plik_%s.pdf",req.queryParams("id"))); // nagłówek
        System.out.println(req.url());
        System.out.println(req.queryParams("id"));
        OutputStream outputStream = null;
        try {
            outputStream = res.raw().getOutputStream();
            outputStream.write(Files.readAllBytes(Paths.get(String.format("invoices/plik_%s.pdf",req.queryParams("id"))))); // response pliku do przeglądarki

        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return req.body();
    };
}




class Airbag{
    String poduszka;
    boolean ma;
    public Airbag(String poduszka, boolean ma) {
        this.poduszka = poduszka;
        this.ma = ma;
    }

    @Override
    public String toString() {
        return "Airbag{" +
                "poduszka='" + poduszka + '\'' +
                ", ma=" + ma +
                '}';
    }
}
class Car {
    private String uuid;
    public String model;
    public String rok;
    private String color;
    public ArrayList<Airbag> poduszkiArr;


    Car(String model, String rok, String color, ArrayList<Airbag> poduszkiArr) {
        this.model = model;
        this.rok = rok;
        this.color = color;
        this.poduszkiArr = poduszkiArr;
    }

    @Override
    public String toString() {
        return "Car{" +
                "uuid=" + uuid +
                ", model='" + model + '\'' +
                ", rok='" + rok + '\'' +
                ", color='" + color + '\'' +
                ", poduszkiArr=" + poduszkiArr +
                '}';
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUUID() { return uuid;}

    public String getModel() {
        return model;
    }

    public String getRok() {
        return rok;
    }

    public String getColor() {
        return color;
    }
}

class UpdateData {
    public String model;
    public String rok;
    private String uuid;

    UpdateData(String model, String rok, String uuid) {
        this.uuid = uuid;
        this.model = model;
        this.rok = rok;

    }
    @Override
    public String toString() {
        return "UpdateData{" +
                "uuid=" + uuid +
                ", model='" + model + '\'' +
                ", rok='" + rok +'}';
    }
    public String getUUID() { return uuid;}
}