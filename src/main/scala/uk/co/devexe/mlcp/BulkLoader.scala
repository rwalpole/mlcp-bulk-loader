package uk.co.devexe.mlcp

import java.io.File

import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.sys.process._

/**
  * This Bulk Loader extends MarkLogic Content Pump tp (MLCP) to bulk load multiple directories of data into
  * a running instance of MarkLogic NoSQL Database. It takes either a text input file which contains a list of
  * directory paths or a directory name from which it then attempts to load the sub-directories contained within.
  * Which method to use depends on the location of your data and whether you want to be highly selective or not.
  */
object BulkLoader {

    def main(args: Array[String]):Unit = {
        if(args.length != 7) {
            return usage()
        }
        val inputFilePath = args(0)
        val server = new Server(args(1),args(2))
        val account = new Account(args(3),args(4))
        val dataType = args(5)
        val permissions = args(6)
        val loader = new BulkLoader(server,account,dataType, permissions, new Executor())
        loader.run(new File(inputFilePath))
    }

    def usage() {
        println("Usage:")
        println("\tjava -jar mlcp-bulk-loader-1.0.jar <input-file-name-or-directory-path> <hostname> <port> <username> <password> <datatype> '<permissions>'")
    }
}

class BulkLoader(server: Server, account: Account, dataType: String, permissions: String, executor: Executable) {

    val logger = LoggerFactory.getLogger(classOf[BulkLoader])

    val os = sys.props("os.name").toLowerCase

    def run(file: File) {
        if(file.exists()){
            val result =
                if(file.isFile) {
                    processFile(file)
                } else {
                    processDirectory(file)
                }
            logger.info("Loaded data from " + result + " directories")
        } else {
            logger.error("File or folder does not exist: " + file.getPath)
        }
    }

    private def panderToWindows(command: Seq[String]) = os match {
        case x if x contains "windows" => Seq("cmd", "/C") ++ command
        case _ => command
    }

    /**
      * Given a directory attempt to load the contents of all subdirectories with MLCP
      *
      * @param directory
      */
    def processDirectory(directory: File): Int = {
        val subDirs = directory.listFiles.filter(_.isDirectory)
        subDirs map { subDir =>
            runMlcp(directory.getAbsolutePath + "\\" + subDir.getName)
        }
        subDirs.length
    }

    /**
      * Given a file attempt to load the contents of the directory names listed inside the file with MLCP
      *
      * @param directoryListFile
      */
    def processFile(directoryListFile: File): Int = {
        val directories = readFile(directoryListFile)
        directories map { directory =>
            runMlcp(directory)
        }
        directories.length
    }

    def runMlcp(inputDirectory: String): Unit = {

        val command = Seq(
            "mlcp","import",
            "-host",server.hostname,
            "-port",server.port,
            "-username",account.username,
            "-password",account.password,
            "-database","rosetta-content",
            "-input_file_path",inputDirectory,
            "-output_uri_replace",getUriReplaceString(inputDirectory),
            "-output_collections",getOutputCollections(dataType),
            "-output_permissions","\"" + permissions + "\""
        )
        val winCommand = panderToWindows(command)
        logger.info("Executing command: " + winCommand.mkString(" "))
        executor.execute(winCommand)
    }

    private def getUriReplaceString(directoryName: String): String = {
        "\"/" + directoryName.replace("\\","/") + ",'" + BulkLoaderConfig.baseDataUri + "'\""
    }

    private def getOutputCollections(dataType: String): String = {
        "\"" + BulkLoaderConfig.defaultCollectionUri + "," + BulkLoaderConfig.baseCollectionUri + dataType + "\""
    }

    def readFile(file: File): List[String] = {
        import resource._
        val items = new ListBuffer[String]()
        for (source <- managed(scala.io.Source.fromFile(file))) {
            for (line <- source.getLines) {
                items += line
            }
        }
        items.toList
    }
}

class Account(val username: String, val password: String)

class Server(val hostname: String, val port: String)

trait Executable {
    def execute(command: Seq[String])
}

class Executor extends Executable {
    import scala.language.postfixOps
    override def execute(command: Seq[String]) = command !
}


