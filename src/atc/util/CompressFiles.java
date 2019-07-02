/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package atc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author cekov
 */
public class CompressFiles extends Thread{

    ArrayList<File> compressed;
    
    public CompressFiles() {
        compressed = new ArrayList<>();
        setDaemon(true);
    }
    
    public void zipAllFiles(){
        File alerts = new File("alert");
        File events = new File("events");
        
        
        File[] alertPaths = alerts.listFiles();
        File[] eventPaths = events.listFiles();
        
        
        FileOutputStream fos;
        try {
            String pattern = "yyyy_MM_dd_hh_mm";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            fos = new FileOutputStream("backup\\backup_"+date+".zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            if (alertPaths.length > 0){
                addFilesToZip(zipOut, alertPaths);
            }
            if(eventPaths.length > 0){
                addFilesToZip(zipOut, eventPaths);
            }
            
            zipOut.flush();
            zipOut.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CompressFiles.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CompressFiles.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void addFilesToZip(ZipOutputStream zipOut, File[] files){
        try {
            byte[] buffer = new byte[1024];
            for(File f : files){
                while (!Files.isReadable(f.toPath())){
                    Thread.sleep(20);
                }
                ZipEntry entry = new ZipEntry(f.getName());
                zipOut.putNextEntry(entry);
                FileInputStream fis = new FileInputStream(f);
                int length;
                while((length = fis.read(buffer)) > 0)
                    zipOut.write(buffer,0,length);
                fis.close();
                zipOut.closeEntry();
                compressed.add(f);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(CompressFiles.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CompressFiles.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(60_000);
                zipAllFiles();
                for(File f : compressed)
                    if(f.delete())
                compressed = new ArrayList<>();
            } catch (InterruptedException ex) {
                Logger.getLogger(CompressFiles.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
}
