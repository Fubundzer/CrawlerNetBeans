/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fastemailandfacebookchecker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Młody
 */
public class FastEmailAndFacebookChecker {

    private static List<GetEmailAndFb> threadList = new ArrayList<>();
    private static List<Thread> threads = new ArrayList<>();

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        
//        getListOfDomains(args[0]).stream().forEach((String domain) -> {
//        	addToChecking(domain);
//        });
        
    	List <String> domains = getListOfDomains("/home/michalr/Dokumenty/Dla Kotana (1).csv");
  ExecutorService taskExecutor = Executors.newCachedThreadPool();
        
        
    	int i;
    	for(i=0;i<100;i++)
    	{

    		try {
    			addToChecking(domains.get(i));
                       // threads.get(i).start();
                        taskExecutor.submit(threads.get(i));
                        System.out.println("Thread number: " + i);
				Thread.sleep(0);
                
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        
        taskExecutor.shutdown();
        
        /*int i=1;
    	for(String dom : domains){
            addToChecking(dom);
            System.out.println("Thread number: " + domains.indexOf(dom));
            i++;
            if(i==30){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FastEmailAndFacebookChecker.class.getName()).log(Level.SEVERE, null, ex);
                }
                i=1;
            }
        }*/
        // addToChecking("1000karm.pl");
        // System.out.println(threadList.get(0).emails);
        
        /*int i=0;
        int j=0;
        for(String dom : domains){
            addToChecking(dom);
            //System.out.println("Thread number: " + domains.indexOf(dom));
            //for(j=1;j<30;j++){
                threads.get(domains.indexOf(dom)).start();
                System.out.println("Thread number: " + domains.indexOf(dom));
                i++;
                if(i%30==0){
                    for(j=i-30;j<i;j++){
                        threads.get(j).join();
                    }
                }
            //}
        }*/
       //addToChecking("1000slow.pl");
       //addToChecking("2soczewki.pl");
      // addToChecking("24akumulatory.pl");
       
      String out;
       PrintWriter output = createFile("/home/michalr/Dokumenty/test.txt");
        int index;
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(FastEmailAndFacebookChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(GetEmailAndFb geaf : threadList){
        	index =threadList.indexOf(geaf);
                //output.print("email of "+geaf.getDomain()+": ");
                out="";
        for(String _e : threadList.get(index).emails){
            System.out.println("email of "+geaf.getDomain()+": "+ _e);
            //output.print(_e +" ; ");
            out+=_e+" |;|";
        }
        if(!out.equals(""))
            output.println("email of "+geaf.getDomain()+": "+out);
        }
        for(GetEmailAndFb geaf : threadList){
        	index =threadList.indexOf(geaf);
                //output.print("fb_id of "+geaf.getDomain()+": ");
                out="";
        for(String _fb : threadList.get(index).fb_id){
            System.out.println("facebook_id of "+geaf.getDomain()+": " + _fb);
            //output.print(_fb +" |;| ");
            out+=_fb+" |;|";
        }
        if(!out.equals(""))
            output.println("fb_id of "+geaf.getDomain()+": "+out);
        }
        
        output.close();
    }

    private static void addToChecking(String _d) {
        GetEmailAndFb geaf = new GetEmailAndFb(_d);
        threadList.add(geaf);
        Thread thr = new Thread(geaf);
        threads.add(thr);
        //thr.start();
        try {
            Thread.sleep(250);
        } catch (InterruptedException ex) {
            Logger.getLogger(FastEmailAndFacebookChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public static List<String> getListOfDomains(String path) throws IOException {
        List<String> domainList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher m = Pattern.compile("\".*?\",\"(.*?)\",").matcher(line);
                if (m.find()) {
                    domainList.add(m.group(1));
                }
            }
        }
        return domainList;
    }
    
    private static PrintWriter createFile(String fileName){
    	try{
    		File f = new File(fileName);
    		
    		PrintWriter infoToWrite=new PrintWriter(
    				new BufferedWriter(
    						new FileWriter(f)));
    		return infoToWrite;
    	}
    	catch(IOException e){
    		System.out.println("An I/O Error Occured");
    		System.exit(0);
    	}
    	return null;
    }
}
