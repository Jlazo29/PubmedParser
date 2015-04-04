package parsers;

import ingestion.SolrUtils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * Created by jlazo on 3/25/15.
 */
public class FileTraverser  extends java.nio.file.SimpleFileVisitor<Path> {

    public FileVisitResult visitFile(Path p, BasicFileAttributes attr) {
        try{
            //boolean parsed = SolrUtils.parsePMC(p);
            boolean parsed = SolrUtils.parseMedline(p);
            if (parsed){
                //System.out.println("Successfully parsed " + p.getFileName() + ". Located on folder: " + p.getParent().getFileName());
            }
        }catch(Exception e){
            System.out.println("!Could not parse file: " + p.getFileName() + ". Located on folder: " + p.getParent().getFileName());
            e.printStackTrace();
        }
        return CONTINUE;
    }

    public FileVisitResult preVisitDirectory(Path var1, BasicFileAttributes var2) throws IOException{
        return CONTINUE;
    }
}
