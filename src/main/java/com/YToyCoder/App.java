package com.YToyCoder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

/**
 * Hello world!
 *
 */
public class App 
{
  public static void main( String[] args )
  {
    System.out.println("arg count %s".formatted(args.length));
    for (String arg : args) {
      System.out.println(arg);
    }

    // source range [output]
    if (args.length < 2) {
      System.out.println("need source and splitting range ");
      return;
    }

    String[] path_and_name = get_source_path_and_filename(args[0]);
    int[] range = get_split_range(args[1]);
    String output = get_output(args);
    split(path_and_name[0], path_and_name[1], range, output);
  }

  static void split(String path, String filename, int[] range, String output) {
    String sep = java.io.File.separator;
    PdfReader reader = null;
    PdfCopy copy = null;
    Document doc = null;
    try {
        reader = new PdfReader(path + sep + filename);
        int numberOfPages = reader.getNumberOfPages();
        if (!range_check(range, numberOfPages)) {
          return;
        }

        doc = new Document(reader.getPageSize(1));
        copy = new PdfCopy(doc, new FileOutputStream(output));
        doc.open();

        for (int split_page_number = range[0]; split_page_number <= range[1]; split_page_number++){
          doc.newPage();
          PdfImportedPage page = copy.getImportedPage(reader, split_page_number);
          copy.addPage(page);
        }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (DocumentException e) {
      e.printStackTrace();
    } finally {
        if (reader != null)
          reader.close();
        if (copy != null)
          copy.close();
        if (doc != null)
          doc.close();
    }
  }

  static String get_output(String[] args) {
    if (args.length < 3) {
      return "./out.pdf";
    }
    return args[2];
  }

  static boolean range_check(int[] split_range, int numberOfPages) {
    if (split_range[0] > numberOfPages || split_range[0] > split_range[1]) {
      System.out.println("not valid range");
      return false;
    }

    if (split_range[1] == -1) {
      split_range[1] = numberOfPages;
    }
    return true;
  }

  static String[] get_source_path_and_filename(String src)
  {
    String[] path_and_filename = new String[2];
    int filename_split_index = src.lastIndexOf('/');

    switch (filename_split_index) 
    {
      case -1:
        path_and_filename[0] = "./";
        path_and_filename[1] = src;
        break;
      default:
        path_and_filename[0] = src.substring(0, filename_split_index);
        path_and_filename[1] = src.substring(filename_split_index);
        break;
    }

    return path_and_filename;
  }

  static Optional<Integer> parsing_int(String string) {
    try {
      return Optional.of(Integer.parseInt(string));
    }catch(NumberFormatException e) {
      return Optional.ofNullable(null);
    }
  }

  static int[] get_split_range(String range_exp) {
    // split by ,
    String[] args = range_exp.split(",");

    if (args.length != 2) {
      System.out.println("range format like: 1,2");
      return null;
    }


    Optional<Integer> option_start = parsing_int(args[0]);
    if (option_start.isEmpty()) {
      System.out.println("range format error, example: 1,2");
      return null;
    }

    int[] range = new int[2];
    range[0] = option_start.get();
    parsing_int(args[1])
      .ifPresentOrElse(
        end -> range[1] = end, 
        () -> range[1] = -1
      );

    return range;
  }
}
