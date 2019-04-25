import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class DIFProcessing {

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner filein = new Scanner(new File("DiF_v1b-sm.csv"));
        int count = 0;
        int start=0;
        while (filein.hasNextLine()) {
            String line = filein.nextLine();
            count++;
            if (count<start) {
                continue;
            }
            String parts[] = line.split(",");
            String urlstr = parts[1];
            System.out.println(urlstr);
            String vurlstr = checkUrl(urlstr);
            if (vurlstr==null) {
//                continue; // skip this one if the url throws an exception ... ie. 404 file not found
            }
            URL url = new URL(vurlstr);
            if (download(vurlstr, "../dif")==null) {
                System.out.println("skipping");
//                continue; // skip this one if we can't download it for some reason
            }
            Thread.sleep(100);
            if (Math.random()<1) {
                Image image = ImageIO.read(url);
                int abunchofints[] = new int[142];
                for (int i = 0; i < 140; i++) {
                    abunchofints[i] = (int) Math.round(Double.parseDouble(parts[4 + i]));
                }
                abunchofints[140] = (int) Math.round(Double.parseDouble(parts[184]));
                abunchofints[141] = (int) Math.round(Double.parseDouble(parts[185]));
                displayImage(image, urlstr, abunchofints);
            }
            System.out.println(count + " images downloaded.");
        }
    }

    private static Path download(String sourceURL, String targetDirectory) throws MalformedURLException {
        URL url = new URL(sourceURL);
        String fileName = sourceURL.substring(sourceURL.lastIndexOf('/') + 1, sourceURL.length());
        File f =  new File(targetDirectory + File.separator + fileName);
        if (f.exists() && f.length()>1000) {
            return null; // file already exists, don't download again
        }
        Path targetPath = f.toPath();
        try {
            Files.copy(url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return targetPath;
    }

    public static String checkUrl(String url) throws MalformedURLException {
        URL obj = new URL(url);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) obj.openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            conn.addRequestProperty("Referer", "google.com");
//            System.out.println("Request URL ... " + url);

            boolean redirect = false;

            // normally, 3xx is redirect
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

//            System.out.println("Response Code ... " + status);

            if (redirect) {

                // get redirect url from "location" header field
                String newUrl = conn.getHeaderField("Location");

                // get the cookie if need, for login
                String cookies = conn.getHeaderField("Set-Cookie");

                // open the new connnection again
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestProperty("Cookie", cookies);
                conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                conn.addRequestProperty("User-Agent", "Mozilla");
                conn.addRequestProperty("Referer", "google.com");

//                System.out.println("Redirect to URL : " + newUrl);
                return newUrl;
            } else {
                return url;
            }
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    public static void displayImage(Image bufferedImage, String title, int boi[]) {
        if (bufferedImage==null) {
            return;
        }
        Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
        g2d.drawRect(boi[0],boi[1],boi[2]-boi[0],boi[3]-boi[1]);
        for (int i=4; i<boi.length; i+=2) {
            int x= boi[i];
            int y= boi[i+1];
            g2d.drawRect(x, y, 2, 2);
        }
        ImageIcon icon=new ImageIcon(bufferedImage);
        JFrame frame=new JFrame(title + boi[140] + "," + boi[141]);
        frame.setLayout(new FlowLayout());
        frame.setSize(bufferedImage.getWidth(null)+150,bufferedImage.getHeight(null)+80);
        JLabel lbl=new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
