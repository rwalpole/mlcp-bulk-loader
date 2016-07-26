package uk.co.devexe.mlcp

import java.io.File

import org.junit.Assert._
import org.junit._

@Test
class BulkLoaderTest {

    @Ignore
    @Test
    def testProcessFile() {
        val file = new File("src/test/resources/sample.txt")
        val processor = new BulkLoader(new Server("localhost","8008"),new Account("admin","admin"),"versioned-acts")
        val result = processor.processFile(file)
    }

    @Ignore
    @Test
    def testProcessDirectory() {
        val directory = new File("src/test/resources/sample")
        val processor = new BulkLoader(new Server("localhost","8008"),new Account("admin","admin"),"versioned-acts")
        val result = processor.processDirectory(directory)
    }

}


