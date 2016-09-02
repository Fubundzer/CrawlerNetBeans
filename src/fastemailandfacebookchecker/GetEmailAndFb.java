/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fastemailandfacebookchecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author Młody
 */
public class GetEmailAndFb implements Runnable {

    private String domain;
    public List<String> emails = new ArrayList<>();
    public List<String> fb_id = new ArrayList<>();

    private static final String REGEX = "(\\b[a-zA-Z0-9.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z0-9.-]+\\b)|www\\.facebook\\.com/plugins/likebox\\.php\\?href=(.*?)\"|www\\.facebook\\.com/plugins/like\\.php\\?href=(.*?)\"|www\\.facebook\\.com/(.*?)\"";

   // private static final String REGEX = "([A-Za-z0-9._\\%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4})|www\\.facebook\\.com/(.*?)\"";
    
    //|www\\.facebook\\.com/plugins/likebox\\.php\\?href=(.*?)\"";

    
    public GetEmailAndFb(String domain) {
        this.domain = domain;
    }
    
    public String getDomain(){
    	return domain;
    }
    
    private volatile boolean isRunning = true;
    
    @Override
    public void run() {
        System.out.println("run");
        try {
            tryByHttp();
        } catch (IOException ex) {
            try {
                tryByHttps();
            } catch (IOException ex1) {
                System.out.println("Wgl sie nie udalo");
            }
        }
       // System.out.println("Done my job");
    }

    
    /*@Override
    public void run() {
        System.out.println("run");
        try {
            while(!Thread.currentThread().isInterrupted()){
            tryByHttp();
            }
            cancel();
            System.out.println("Koniec watku");
        }  catch (IOException ex1) {
                System.out.println("Wgl sie nie udalo");
            }
    }*/

       public void cancel (){
           Thread.currentThread().interrupt();
       }
    
    private void tryByHttp() throws MalformedURLException, IOException {
        URL _url = new URL("http://" + domain);
        HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
            checkPage(new BufferedReader(new InputStreamReader((connection.getInputStream()))));
        }else{
            System.out.println(connection.getResponseCode());
        }
    }

    private void tryByHttps() throws IOException {
        URL _url = new URL("https://" + domain);
        HttpsURLConnection connection = (HttpsURLConnection) _url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
            checkPage(new BufferedReader(new InputStreamReader((connection.getInputStream()))));
        }else{
            System.out.println(connection.getResponseCode());
        }
    }

    private void checkPage(BufferedReader br) throws IOException {
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        System.out.println(sb);
        Matcher m = Pattern.compile(REGEX).matcher(sb);
        while (m.find()) {
            if (m.group(1) != null && !m.group(1).isEmpty()) {
                emails.add(m.group(1));
            } else if(m.group(2) != null){
                fb_id.add(m.group(2));
            }else if(m.group(3) != null){
                fb_id.add(m.group(3));
                System.out.println("~~~~~~~~~~~~!!!!!!!!!!!!1");
            }else if(m.group(4) != null&&!m.group(4).startsWith("2008/fbml")){
                fb_id.add(m.group(4));
            }
        }
        //Thread.currentThread().interrupt();
    }
}

