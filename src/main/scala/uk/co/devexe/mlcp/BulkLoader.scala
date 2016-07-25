package uk.co.devexe.mlcp

import java.io.File

import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.sys.process._

/**
  * This Bulk Loader extends MarkLogic Content Pump tp (MLCP) to bulk load multiple directories of data into
  * a running instance of MarkLogic NoSQL Database. It requires a text input file which contains subdirectory names
  * and assumes these directories are all within the same root location which is specified in the application.conf file
  *
  * @param fileToProcess
  */
object BulkLoader {

    def main(args: Array[String]):Unit = {
        if(args.length != 5) {
            return usage()
        }
        val inputFilePath = args(0)
        val server = new Server(args(1),args(2))
        val account = new Account(args(3),args(4))
        val dataType = args(5)
        val loader = new BulkLoader(new File(inputFilePath))
        loader.process(server, account, dataType)
    }

    def usage() {
        println("Usage:")
        println("\tjava -jar mlcp-bulk-loader-1.0.jar <input-file-path> <hostname> <port> <username> <password> <datatype>")
    }
}

class BulkLoader(fileToProcess: File) {

    val logger = LoggerFactory.getLogger(classOf[BulkLoader])

    val os = sys.props("os.name").toLowerCase

    val rosettaCollectionUri = "http://lexisnexis.co.uk/collection/rosetta-xml"

    private def panderToWindows(command: Seq[String]) = os match {
        case x if x contains "windows" => Seq("cmd", "/C") ++ command
        case _ => command
    }

    def process(server: Server, account: Account, dataType: String) {
        val items = readFile()
        items map { item =>
            val command = Seq(
                "mlcp","import",
                "-host",server.hostname,
                "-port",server.port,
                "-username",account.username,
                "-password",account.password,
                "-database","rosetta-content",
                "-input_file_path",BulkLoaderConfig.dataDir + dataType + "\\" + item,
                "-output_uri_replace",getUriReplaceString(dataType,item),
                "-output_collections",getOutputCollections(dataType)
            )
            val winCommand = panderToWindows(command)
            logger.info("Executing command: " + winCommand.mkString(" "))
            winCommand !
        }
    }

    private def getReplacePath(): String = {
        "/" + BulkLoaderConfig.dataDir.replace("\\","/")
    }

    private def getUriReplaceString(dataType: String, item: String): String = {
        "\"" + getReplacePath() + dataType + "/" + item + ",'" + BulkLoaderConfig.baseDataUri + "'\""
    }

    private def getOutputCollections(dataType: String): String = {
        "\"" + BulkLoaderConfig.defaultCollectionUri + "," + BulkLoaderConfig.baseCollectionUri + dataType + "\""
    }

    def readFile(): List[String] = {
        import resource._
        val items = new ListBuffer[String]()
        for (source <- managed(scala.io.Source.fromFile(fileToProcess))) {
            for (line <- source.getLines) {
                items += line
            }
        }
        items.toList
    }
}

class Account(val username: String, val password: String)
class Server(val hostname: String, val port: String)

