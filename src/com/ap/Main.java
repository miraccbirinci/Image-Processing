package com.ap;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ImageReader imageReader = getType("type6.advprog");//Path e istediğimiz türdeki resmi verebiliriz hocam ben type 6 verdim.
														   //type 5 vererek de ekran goruntusu aldum proje dosyasi icerisinde yer aliyor
        imageReader.read();
        imageReader.parse();
        imageReader.display();

    }

    public static ImageReader getType(String path){
        try {
            FileInputStream fis = new FileInputStream(path);//Verilen yoldaki dosyayı okuyoruz. Scanner kullanmamamızın
            //sebebi ise sadece text-based dosyaları okuması ama bizim hem text hem de binary dosyaları okumamız gerekiyor.
            //Bu aşamada dosya text mi binary mi bilemiyoruz. O yüzden FileInputStream kullanıyoruz.
            try {
                int type = fis.read() - 48;//Dosyanın türünü belirten ilk byteı okuyoruz. Okuduğumuz değeri 48 den çıkarma
                //sebebimiz ise ascii tablosunda rakamların byte değeri 48den başlıyor (48-0, 49-1, 50-2 ...).
                //yani okuduğumuz sayı 3 ise onun byte değeri 51, 48den çıkarınca gerçek değerine erişiyoruz.
                switch (type){//Okuduğumuz türe göre gereken nesneyi döndürüyoruz.
                    case 1:
                        return new Type1(path);
                    case 2:
                        return new Type2(path);
                    case 3:
                        return new Type3(path);
                    case 5:
                        return new Type5(path);
                    case 6:
                        return new Type6(path);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}

/**
 *  Dosyalardaki ilk sayı o resmin türünü  belirtiyor. 1 ise siyah-beyaz text, 2 ise gri text, 3 ise renkli text,
 *  5 ise gri binary, 6 ise renkli binary.
 *  İkinci sayı resmin genişliği,
 *  üçüncü sayı resmin yüksekliğini
 *  Varsa dördüncü (genellikle 255) resmin piksellerinin alabileceği max değeri belirtir. Visual studio dersinde de buna benzer bir ornek yapmistiniz hocam
 *  Geri kalan değerler ise resimlerin renklerinin belirten piksel değerleridir.
 *
 *  Type1 siyah-beyaz resimleri okuyan sınıf. Piksel rengi ya siyah ya da beyaz başka bir renk veya gri tonu yok.
 *  Eğer o pikselin değeri 0 ise pikselin rengi siyah, 1 ise beyaz olacaktır.
 *
 *  Inteface dört metotdan oluşmakta: read(String path), parse(), display(Color[][] imageColors, int width, int height)
 *  ve display(). Parametreli display interfacei implement eden sınıfların verilerini göndererek çağıracakları metot.
 *  Interfaceler içinde normalde metotun gövdesi yazılmaz çünkü intefacelerden nesne oluşturulmaz tanım tarafı
 *  alt sınıflara bırakılr. 'default' olarak tanımlanan metotlar alt sınıfların hepsi tarafından sahip olunur.
 *  read(String path): path deki dosyayı okur ve piksel değerlerini, image isimli arraye aktarır.
 *  parse(): image arrayindeki değerleri Color türünde renklere çevirir ve imageColors arrayine aktarır.
 *  display(): Alt sınıfların verileri ile diğer display metodunu çağırmaları için ve
 *  resmin Java Swing kullanılarak görüntülenmesini sağlar.
 *
 *  Strategy Design Pattern kullanıyorum. Resimleri işlemek için farklı algoritmalar var ama hepsinin yapacağı iş aynı.
 *  Dosyadan piksel değerlerini okuma, değerlerin renklere çevirme ve ekranda gösterme.
 *  bu Interface alt sınıflara bunları y yapacağımızi bize birakmistiniz hocam (read, parse, display)
 *  main metodunda kullanırken ise ImageReader türünü kullandım. Böylece okuduğumuz resim hangi türde olursa olsun
 *  read, parse ve display metodu olmak zorunda ama o metotları mainde kullanırken gelen resim türüne göre özel algoritmalar
 *  kullanılacak. Böylece polymorphism i sağlamaış olacağım.
 *
 */
interface ImageReader{

    void read();
    void parse();
    default void display(Color[][] imageColors, int width, int height){
        System.out.println(width);
        Panel panel = new Panel(imageColors, width, height); //resmin renk arrayini, genişliğini ve yüksekliğini vererek, resmin görüntüleneceği panel nesnesini oluşturuyoruz.
        JFrame frame = new JFrame();                        //JFrame oluşturuyoruz. Java GUI nin olmazsa olmazı.
        frame.add(panel);                                   //Paneli JFrame e ekliyoruz.
        frame.setVisible(true);
        frame.setSize(width, height);                       //frame in boyutlarını tanımlayıp görünür yapıyoruz.
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //Gelecek ekranın kapama tuşuyla uygulamanın da duracağını belirtiyoruz.
    }
    void display();

}
class Type1 implements ImageReader{
    /*
    *   Text dosyaları Javada genellikle Scanner kullanılarak okunuyor. 
    *
    * */
    Scanner scanner;    //Dosyayı okumada kullanacağımız Scanner.
    int[][] image;        //Resim piksel değerlerinin tutulacağı array.
    int width;          //Resmin genişliği.
    int height;         //Resmin yüksekliği.
    Color[][] imageColors;//Resimlerin piksel değerlerinin renge dönüştürülmüş halinin tutulacağı array.
    String path;

    Type1(String path){
        this.path = path;
    }

    @Override
    public void read(){
        try {
            scanner = new Scanner(new File(path));//Verilen yoldaki dosyayı okuyacak Scanner nesnesi oluşturuyoruz.
            int type = scanner.nextInt();         //Dosya türünü okuyoruz.
            width = scanner.nextInt();          //Resmin genişliğini alıyoruz
            height = scanner.nextInt();         //Resmin yüksekliğini alıyoruz
            image = new int[height][width];    //Resimdeki piksel sayısı genişlik x yükseklik olacağı için image arrayinin size ını ona göre veriyoruz.
            for(int i=0;i<height;i++) {
                for(int j=0;j<width;j++) {
                    image[i][j]=scanner.nextInt();  //Her pikselin değerini sırasıyla okuyup image arrayinde tutuyoruz.
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse(){
        imageColors = new Color[height][width];    //Resimdeki piksel sayısı kadar renk tutulacak.
        for(int i=0;i<height;i++) {                  //Image arrayini dolaşıyoruz.
            for(int j=0;j<width;j++) {
                if (image[i][j] == 0){        //Eğer o pikselin değeri 0 ise renk siyah değil ise beyaz.
                    imageColors[i][j] = Color.BLACK;
                }else{
                    imageColors[i][j] = Color.WHITE;
                }
            }
        }
    }

    public void display(){
        this.display(imageColors, width, height);
    }

}

class Type2 implements ImageReader{

    Scanner scanner;    //Dosyayı okumada kullanacağımız Scanner.
    int[][] image;        //Resim piksel değerlerinin tutulacağı array.
    int width;          //Resmin genişliği.
    int height;         //Resmin yüksekliği.
    Color[][] imageColors;//Resimlerin piksel değerlerinin renge dönüştürülmüş halinin tutulacağı array.
    String path;

    Type2(String path){
        this.path = path;
    }

    @Override
    public void read() {
        try {
            scanner = new Scanner(new File(path));//Verilen yoldaki dosyayı okuyacak Scanner nesnesi oluşturuyoruz.
            scanner.nextInt();         //Dosya türünü okuyoruz.
            width = scanner.nextInt();          //Resmin genişliğini alıyoruz
            height = scanner.nextInt();         //Resmin yüksekliğini alıyoruz
            image = new int[height][width];    //Resimdeki piksel sayısı genişlik x yükseklik olacağı için image arrayinin size ını ona göre veriyoruz.
            for(int i=0;i<height;i++) {
                for(int j=0;j<width;j++) {
                    image[i][j]=scanner.nextInt();  //Her pikselin değerini sırasıyla okuyup image arrayinde tutuyoruz.
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void parse() {
        imageColors = new Color[height][width];    //Resimdeki piksel sayısı kadar renk tutulacak.
        for(int i=0;i<height;i++) {                  //Image arrayini dolaşıyoruz.
            for(int j=0;j<width;j++) {
                imageColors[i][j] = new Color(image[i][j], image[i][j], image[i][j]);
            }
        }

    }

    public void display(){
        this.display(imageColors, width, height);
    }


}

/**
 *  Type3 renkli resimleri okuyan sınıf. Piksel rengi RGB üzerinden gösterilecek.
 *  Dosyadaki piksel değerleri sırasıyla her üçü aslında bir rengi temsil ediyor. O yüzden bir piksel için üç değer okumamız gerek.
 *  Üç değerin ilki R yani rengin kırmızı değerini, ikincisi G yani yeşil değerini, üçüncüsü ise B yani mavi değeri ifade eder.
 *
 *  Sınıf üç metotdan oluşmakta: read(String path), parse() ve display().
 *  read(String path): path deki dosyayı okur ve piksel değerlerini, image isimli arraye aktarır.
 *  parse(): image arrayindeki değerleri Color türünde renklere çevirir ve imageColors arrayine aktarır.
 *  display(): Resmin Java Swing kullanılarak görüntülenmesini sağlar.
 *
 */
class Type3 implements ImageReader{
    Scanner scanner;    //Dosyayı okumada kullanacağımız Scanner.
    int[][][] image;    //Resim piksel değerlerinin tutulacağı array.
    int width;          //Resmin genişliği.
    int height;         //Resmin yüksekliği.
    Color[][] imageColors;//Resimlerin piksel değerlerinin renge dönüştürülmüş halinin tutulacağı array.
    String path;

    Type3(String path){
        this.path = path;
    }

    @Override
    public void read(){
        try {
            scanner = new Scanner(new File(path));//Verilen yoldaki dosyayı okuyacak Scanner nesnesi oluşturuyoruz.
            int type = scanner.nextInt();         //Dosya türünü okuyoruz.
                    //Tür 3 ise işleme devam değil ise dosyanın yanlış türde olduğunu belirtiyoruz.
            width = scanner.nextInt();        //Resmin genişliğini alıyoruz
            height = scanner.nextInt();       //Resmin yüksekliğini alıyoruz
            scanner.nextInt();
            image = new int[height][width][3]; //Her piksel için üç değer okuyacağımız için arrayin boyutunu piksel sayının üç katı olarak tanımlıyoruz.
            for(int i=0; i<height; i++) {
                for(int j=0; j<width; j++) {
                    for (int k=0; k<3; k++) {
                        image[i][j][k]=scanner.nextInt(); //Her pikselin değerini sırasıyla okuyup image arrayinde tutuyoruz.
                    }
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void parse(){
        imageColors = new Color[height][width]; //Resimdeki piksel sayısı kadar renk tutulacak.
        for(int i=0; i<height; i++) {             //Image arrayini dolaşıyoruz.
            for(int j=0; j<width; j++) {    //j yi üç üç arttırmamızın sebebi her renk için üç değer alacağız ama her seferinde bir renk ekleyeceğiz yani image arrayini daha rahat dolaşabilmek için
                int r = image[i][j][0];     //r değerini okuyoruz.
                int g = image[i][j][1];   //g değerini okuyoruz.
                int b = image[i][j][2];   //b değerini okuyoruz.
                imageColors[i][j] = new Color(r, g, b); //Üç değeri kullanarak renk oluşturup imageColors arrayine ekliyoruz.
            }
        }
    }

    public void display(){
        this.display(imageColors, width, height);
    }

}

class Type5 implements ImageReader{

    FileInputStream fis;
    int width;          //Resmin genişliği.
    int height;         //Resmin yüksekliği.
    int range;
    Color[][] imageColors;//Resimlerin piksel değerlerinin renge dönüştürülmüş halinin tutulacağı array.
    int[][] image;        //Resim piksel değerlerinin tutulacağı array.
    String path;

    Type5(String path){
        this.path = path;
    }


    @Override
    public void read() {
        try {
            fis = new FileInputStream(path);
            int fileType = fis.read() - 48;//Dosyanın tipini okuyoruz ama daha önceden okuduğumuz için kullanmayacağız.

            //Dosyadaki width, height ve range değerlerini okumak için üç kere çalışacak döngü yazıyoruz.
            //fis.read() karakter karakter okur yani dosyadaki değer 321 ise önce 3ü sonra 2yi sonra 1i tek tek okumak gerek.
            //O yüzden ilk karakteri okuyup boşluk(whitespace) mu yoksa bir değer mi ona göre hareket ediyoruz.
            int readByte = fis.read();
            for (int i=0; i<3; i++){
                StringBuilder value = new StringBuilder();//Okuduğumuz byteları string olarak toparlayabileceğimiz bir nesne
                while (Character.isWhitespace(readByte)){//Okuduğumuz karakter boşluksa sonrakini okumaya devam ediyoruz.
                    readByte = fis.read();
                }
                while (!Character.isWhitespace(readByte)){////Okuduğumuz boşluk değilse sonrakini okumaya devam ediyoruz.
                    value.append(readByte - 48);        //Okuduğumuz değeri value değişkenine ekliyoruz.
                                                        //String builder teker teker verdiğimiz değerleri sırasıyla birleştiriyor.
                    readByte = fis.read();
                }

                if(i==0) {//For un ilk döngüdeysek okuduğumuz değer resmin genişliği
                    width = Integer.parseInt(value.toString());
                }
                else if(i==1){//For un ikinci döngüdeysek okuduğumuz değer resmin yüksekliği
                    height = Integer.parseInt(value.toString());
                }else {//For un üçüncü döngüdeysek okuduğumuz değer piksellerin max değeri
                    range = Integer.parseInt(value.toString());
                }
            }

            image = new int[height][width];
            for(int i=0;i<height;i++) {
                for(int j=0;j<width;j++) {
                    image[i][j]= fis.read();  //Her pikselin değerini sırasıyla okuyup image arrayinde tutuyoruz.
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parse() {
        imageColors = new Color[height][width];    //Resimdeki piksel sayısı kadar renk tutulacak.
        for(int i=0;i<height;i++) {                  //Image arrayini dolaşıyoruz.
            for(int j=0;j<width;j++) {
                imageColors[i][j] = new Color(image[i][j], image[i][j], image[i][j]);
            }
        }
    }

    public void display(){
        this.display(imageColors, width, height);
    }

}

class Type6 implements ImageReader{

    FileInputStream fis;
    int width;          //Resmin genişliği.
    int height;         //Resmin yüksekliği.
    int range;
    int[][][] image;        //Resim piksel değerlerinin tutulacağı array.
    Color[][] imageColors;//Resimlerin piksel değerlerinin renge dönüştürülmüş halinin tutulacağı array.
    String path;

    Type6(String path){
        this.path = path;
    }

    @Override
    public void read() {
        try {
            fis = new FileInputStream(path);
            int fileType = fis.read() - 48;

            int readByte = fis.read();
            for (int i=0; i<3; i++){
                StringBuilder value = new StringBuilder();
                while (Character.isWhitespace(readByte)){
                    readByte = fis.read();
                }
                while (!Character.isWhitespace(readByte)){
                    value.append(readByte - 48);
                    readByte = fis.read();
                }

                if(i==0) {
                    width = Integer.parseInt(value.toString());
                }
                else if(i==1){
                    height = Integer.parseInt(value.toString());
                }else {
                    range = Integer.parseInt(value.toString());
                }
            }

            image = new int[height][width][3];
            for(int i=0;i<height;i++) {
                for(int j=0;j<width;j++) {
                    for (int k=0; k<3; k++) {
                        image[i][j][k] = fis.read();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void parse() {

        imageColors = new Color[height][width]; //Resimdeki piksel sayısı kadar renk tutulacak.
        for(int i=0; i<height; i++) {             //Image arrayini dolaşıyoruz.
            for(int j=0; j<width; j++) {    //j yi üç üç arttırmamızın sebebi her renk için üç değer alacağız ama her seferinde bir renk ekleyeceğiz yani image arrayini daha rahat dolaşabilmek için
                int r = image[i][j][0];     //r değerini okuyoruz.
                int g = image[i][j][1];   //g değerini okuyoruz.
                int b = image[i][j][2];   //b değerini okuyoruz.
                imageColors[i][j] = new Color(r, g, b); //Üç değeri kullanarak renk oluşturup imageColors arrayine ekliyoruz.
            }
        }

    }

    public void display(){
        this.display(imageColors, width, height);
    }


}

class Panel extends JPanel{// Java Swing in JPanel sınıfından extends ediyoruz
    Color[][] colorArray; //Ekranda gösterilecek resmin piksel renklerinin tutulduğu array
    int width;          //Ekranda gösterilecek resmin genişliği
    int height;         //Ekranda gösterilecek resmin yüksekliği

    Panel(Color[][] array, int width, int height){
        this.colorArray = array;
        this.width = width;
        this.height = height;
    }

    @Override
    protected void paintComponent(Graphics g) {//JPanel e ait metot. Eklediğimiz panelin istediğimiz gibi boyamak için kullanıyoruz.
        super.paintComponent(g);
        for(int y=0;y<height;y++) {
            for(int x=0;x<width;x++) {
                g.setColor(colorArray[y][x]);// Pikselin rengini alıp
                g.fillRect(x, y, 1, 1);//resimdeki yerini boyuyoruz.
            }
        }


    }


}

