package uk.co.devexe.mlcp

import java.io.File

import org.junit.Assert._
import org.junit._

@Test
class BulkLoaderTest {

    @Test
    def testProcess() = {
        val file = new File("src/test/resources/test.txt")
        val processor = new BulkLoader(file)
        val result = processor.process("localhost","8008","admin","admin","versioned-sis")

    }

//    @Test
//    def testKO() = assertTrue(false)

}


