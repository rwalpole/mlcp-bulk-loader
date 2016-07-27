package uk.co.devexe.mlcp

import java.io.File

import org.junit._

@Test
class BulkLoaderTest {

    @Test
    def testProcessFile() {
        val file = new File("src/test/resources/sample.txt")
        val processor = new BulkLoader(new Server("localhost","8008"),new Account("admin","admin"),"versioned-acts", new TestExecutor())
        val result = processor.processFile(file)
        Assert.assertEquals(2,result)
    }

    @Test
    def testProcessDirectory() {
        val directory = new File("src/test/resources/sample")
        val processor = new BulkLoader(new Server("localhost","8008"),new Account("admin","admin"),"versioned-acts", new TestExecutor())
        val result = processor.processDirectory(directory)
        Assert.assertEquals(2,result)
    }

}

class TestExecutor extends Executable {
    override def execute(command: Seq[String]) = println(command.mkString(" "))
}


