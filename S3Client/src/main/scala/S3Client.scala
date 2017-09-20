package com.kainos.s3streamer

import java.io.{ File, FileInputStream }
import java.nio.file.Paths
import java.util

import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.{
  AWSStaticCredentialsProvider,
  PropertiesCredentials
}
import com.amazonaws.services.s3.model.{ Bucket, S3Object, S3ObjectInputStream }
import com.amazonaws.services.s3.{ AmazonS3, AmazonS3ClientBuilder }

import scala.collection.JavaConversions._

class S3Client(credentialsPath: String,
               bucketName: String,
               region: String,
               createBucket: Boolean = false) {

  private val clientConf: ClientConfiguration = new ClientConfiguration()
  private val s3Client: AmazonS3 = AmazonS3ClientBuilder
    .standard()
    .withClientConfiguration(clientConf)
    .withCredentials(new AWSStaticCredentialsProvider(getAwsCredentials))
    .withRegion(region)
    .build()

  println(s3Client.listBuckets().map(_.getName))
  println(s3Client.listBuckets().exists(_.getName.equals(bucketName)))
  println(bucketName)

  (s3Client.listBuckets().exists(_.getName.equals(bucketName)), createBucket) match {
    case (false, true) => s3Client.createBucket(bucketName)
    case (true, true) =>
    case (true, false) =>
    case (false, false) =>
      throw new S3ClientException(
        s"Couldn't find bucket with name $bucketName, and createBucket is false."
      )
  }

  def bucketList: util.List[Bucket] = s3Client.listBuckets()

  def getAsStream(objectName: String): S3ObjectInputStream =
    s3Client.getObject(bucketName, objectName).getObjectContent

  def get(objectName: String): S3Object =
    s3Client.getObject(bucketName, objectName)

  def put(file: File, name: String) = s3Client.putObject(bucketName, name, file)

  @throws[S3ClientException]
  private[s3streamer] def getAwsCredentials: PropertiesCredentials = {
    val is = new FileInputStream(new File(Paths.get(credentialsPath).toString))
    if (is != null) {
      new PropertiesCredentials(is)
    } else {
      throw new S3ClientException(
        "Unable to load credentials from properties file."
      )
    }
  }
}

class S3ClientException(message: String, cause: Throwable = null)
    extends RuntimeException(message, cause)
