

lazy val projectScalaVersion = "2.11.11"
lazy val projectVersion = "0.1-SNAPSHOT"
lazy val awsClientVersion = "1.11.86"
lazy val akkaVersion = "2.4.8"

version := projectVersion
scalaVersion := projectScalaVersion

lazy val AkkaS3Streamer = (project in file("."))
    .aggregate(S3Client,akkasystem)

lazy val S3Client = (project in file("S3Client"))
    .settings(
      scalaVersion := projectScalaVersion,
      name := "S3Client",
      version := projectVersion,
      libraryDependencies ++= Seq(
        "com.amazonaws" % "aws-java-sdk-core" % awsClientVersion,
        "com.amazonaws" % "aws-java-sdk-s3" % awsClientVersion
      )
    )

lazy val akkasystem = (project in file("akkasystem"))
    .settings(
      scalaVersion := projectScalaVersion,
      name := "akkasystem",
      version := projectVersion,
      resolvers += "Lightbend Repository" at "http://repo.typesafe.com/typesafe/releases/",
      libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-actor" % akkaVersion
      )
    ).dependsOn(S3Client)
