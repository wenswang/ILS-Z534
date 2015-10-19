import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class generateIndex {

	public generateIndex(Analyzer a) throws Exception {
		// TODO Auto-generated constructor stub
		Directory dir = FSDirectory.open(Paths.get("/Users/wensiwang/Desktop/Z534/Assignment/Assignment1/result"));
		Analyzer analyzer = a;
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		writer = new IndexWriter(dir, iwc);
		}
	
	IndexWriter writer;
	
	// This function reads all the file in the corpus
	public  void readFiles() throws Exception{
		
		final File folder = new File("/Users/wensiwang/Desktop/Z534/Assignment/Assignment1/corpus");
		for (final File fileEntry : folder.listFiles()) {
			File file = new File("/Users/wensiwang/Desktop/Z534/Assignment/Assignment1/corpus/"+fileEntry.getName()+"");
			if(!FilenameUtils.getExtension(file.getName()).equals("trectext")){
				file.delete();
				}
			}
		for (final File fileEntry : folder.listFiles()) {
			File file = new File("/Users/wensiwang/Desktop/Z534/Assignment/Assignment1/corpus/"+fileEntry.getName()+"");
			parseFile(file);			
			}
		writer.forceMerge(1);
		writer.commit();
		writer.close();
		}
	
	// This function does the parsing using the apache commons library and puts the data in the luceneDoc
	public void parseFile(File file) throws Exception{
		String fileToString = FileUtils.readFileToString(file, null);
		String lineSperateString = System.getProperty("line.separator");
		String divideByDoc[] = fileToString.split("</DOC>"+lineSperateString+"<DOC>");
		
		for(int i=0;i<divideByDoc.length;i++){
			Document luceneDoc = new Document();
			
			String first = StringUtils.substringBetween(divideByDoc[i], "<DOCNO>", "</DOCNO>");
			StringBuffer docNo = new StringBuffer();
			docNo.append(first);
			docNo.toString();
			luceneDoc.add(new StringField("DOCNO", docNo.toString(),Field.Store.YES));
			
			String second = StringUtils.substringBetween(divideByDoc[i], "<HEAD>", "</HEAD>");
			StringBuffer head = new StringBuffer();
			head.append(second);
			head.toString();
			luceneDoc.add(new TextField("HEAD", head.toString(),Field.Store.YES));
			
			String third = StringUtils.substringBetween(divideByDoc[i], "<BYLINE>", "</BYLINE>");
			StringBuffer byLine = new StringBuffer();
			byLine.append(third);
			byLine.toString();
			luceneDoc.add(new TextField("BYLINE", byLine.toString(),Field.Store.YES));
			
			String fourth = StringUtils.substringBetween(divideByDoc[i], "<DATELINE>", "</DATELINE>");
			StringBuffer dateLine = new StringBuffer();
			dateLine.append(fourth);
			dateLine.toString();
			luceneDoc.add(new TextField("DATELINE", dateLine.toString(),Field.Store.YES));
			
			StringBuffer text = new StringBuffer();
			String testsString = divideByDoc[i];
			String fifth = null;
			fifth = StringUtils.substringBetween(testsString, "<TEXT>", "</TEXT>");
			text.append(fifth);
			text.append("\\s+");
			testsString = testsString.substring(testsString.indexOf("</TEXT>")+7);
			while(true){
				fifth = StringUtils.substringBetween(testsString, "<TEXT>", "</TEXT>");
				if(testsString.indexOf("</TEXT>")==-1){
					break;
					}
				text.append(fifth);
				text.append("\\s+");
				testsString = testsString.substring(testsString.indexOf("</TEXT>")+7);
				}
			text.toString();
			//System.out.println(text.toString());
			luceneDoc.add(new TextField("TEXT", text.toString(),Field.Store.YES));
	
			writer.addDocument(luceneDoc);
			}
		}
	
	//This function displays the statistics
	public static void extractData(String path) throws Exception{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("/Users/wensiwang/Desktop/Z534/Assignment/Assignment1/result/")));
				
		//Print the total number of documents in the corpus
		System.out.println("Total number of documents in the corpus:"+reader.maxDoc());
		
		//Print the number of documents containing the term "new" in <field>TEXT</field>.
		System.out.println("Number of documents containing the term \"new\" for field \"TEXT\": "+reader.docFreq(new Term("TEXT", "new")));
				
		//Print the total number of occurrences of the term "new" across all documents for <field>TEXT</field>.
		System.out.println("Number of occurences of \"new\" in the field \"TEXT\": "+reader.totalTermFreq(new Term("TEXT","new")));

		
//		int count = 0;
//		Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
//		TermsEnum iterator = vocabulary.iterator();
//		BytesRef byteRef = null;
//		while((byteRef = iterator.next()) != null) {
//			count = count+1;
//			}
//		System.out.println("Size of the vocabulary for this field: "+count);
		 
				
		//Print the size of the vocabulary for <field>content</field>, only available per-segment.
		Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
		System.out.println("Size of the vocabulary for this field: "+vocabulary.size());
				
		//Print the total number of documents that have at least one term for <field>TEXT</field>
		System.out.println("Number of documents that have at least one term for this field: "+vocabulary.getDocCount());
				
		//Print the total number of tokens for <field>TEXT</field>
		System.out.println("Number of tokens for this field: "+vocabulary.getSumTotalTermFreq());
				
		//Print the total number of postings for <field>TEXT</field>
		System.out.println("Number of postings for this field: "+vocabulary.getSumDocFreq());
				
//		Print the vocabulary for <field>TEXT</field>
//		TermsEnum iterator = vocabulary.iterator();
//		System.out.println("\n*******Vocabulary-Start**********");
//		BytesRef byteRef;
//		while((byteRef = iterator.next()) != null) {
//			String term = byteRef.utf8ToString();
//			System.out.print(term+"\n");
//			}
//		System.out.println("\n*******Vocabulary-End**********");
		
		reader.close();
		}
	}