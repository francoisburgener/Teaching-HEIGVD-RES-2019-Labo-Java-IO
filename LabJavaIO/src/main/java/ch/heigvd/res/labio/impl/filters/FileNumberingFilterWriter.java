package ch.heigvd.res.labio.impl.filters;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.BufferOverflowException;
import java.util.logging.Logger;

/**
 * This class transforms the streams of character sent to the decorated writer.
 * When filter encounters a line separator, it sends it to the decorated writer.
 * It then sends the line number and a tab character, before resuming the write
 * process.
 *
 * Hello\n\World -> 1\Hello\n2\tWorld
 *
 * @author Olivier Liechti
 */
public class FileNumberingFilterWriter extends FilterWriter {
  
  private static final Logger LOG = Logger.getLogger(FileNumberingFilterWriter.class.getName());
  
  private int numLine = 0;
  private int previous = 0;
  
  public FileNumberingFilterWriter(Writer out) {
    super(out);
  }
  
  @Override
  public void write(String str, int off, int len) throws IOException {
    
    if(off + len > str.length()){
      throw new BufferOverflowException();
    }
    
    for(int i = off; i < off + len; ++i){
      write(str.charAt(i));
    }
    
  }
  
  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    if(off + len > cbuf.length){
      throw new BufferOverflowException();
    }
    for(int i = off; i < off + len; ++i){
      write(cbuf[i]);
    }
    
  }
  
  @Override
  public void write(int c) throws IOException {
    
    //If it's the firstline we number the first line
    if(numLine == 0){
      writeNewLine();
    }
    
    //if the char is only a \r we write the line + the tab
    if(previous == '\r' && c != '\n'){
      previous = 0;
      writeNewLine();
    }
    
    //display the actual char
    super.write(c);
    previous = c;
    
    if(c == '\n'){
      writeNewLine();
    }
    
  }
  
  private void writeNewLine() throws IOException {
    String toWtrite = Integer.toString(++numLine) + "\t";
    super.write(toWtrite,0,toWtrite.length());
  }
  
}
