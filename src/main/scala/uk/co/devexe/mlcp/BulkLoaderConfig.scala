package uk.co.devexe.mlcp

import com.typesafe.config.ConfigFactory

/**
  * Created by walpolrx on 25/07/2016.
  */
object BulkLoaderConfig {
    private val config =  ConfigFactory.load()
    val baseDataUri = config.getString("mlcp.base-data-uri")
    val baseCollectionUri = config.getString("mlcp.base-collection-uri")
    val defaultCollectionUri = baseCollectionUri + config.getString("mlcp.default-collection-name")

}
