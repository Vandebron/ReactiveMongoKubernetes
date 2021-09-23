import sbt.{Def, Task}
import sbtdocker.DockerPlugin.autoImport.{buildOptions, docker, dockerfile, BuildOptions, ImageId, ImageName}
import sbtdocker.DockerfileBase
import sbtassembly.AssemblyKeys

name := "mongotest"
ThisBuild / organization := "nl.vandebron"

ThisBuild / scalafixDependencies ++= Seq("org.reactivemongo" %% "reactivemongo-scalafix" % "1.0.7")

ThisBuild / scalaVersion := "2.12.10"

ThisBuild / useCoursier := false

ThisBuild / javaOptions += "-Xmx2G"

ThisBuild / javacOptions += "-XX:+UseG1GC"

val javaBaseImage = "openjdk:8"

enablePlugins(sbtdocker.DockerPlugin)

def imageName(projectName: String) =
  docker / imageNames := Seq(
    // Sets the latest tag
    ImageName(s"vdb-services/mongotest:latest")
  )

def microService(
                  projectName: String,
                  ports: Int*
                ): Seq[
  Def.Setting[_ >: Task[Seq[ImageName]] with Task[DockerfileBase] with BuildOptions with Task[ImageId] <: Product with Serializable]
] =
  Seq(
    docker := (docker dependsOn AssemblyKeys.assembly).value,
    docker / buildOptions := BuildOptions(
      cache = true,
      removeIntermediateContainers = BuildOptions.Remove.Always,
      pullBaseImage = BuildOptions.Pull.IfMissing
    ),
    docker / dockerfile := {
      val artifact = (AssemblyKeys.assembly / AssemblyKeys.assemblyOutputPath ).value
      new sbtdocker.mutable.Dockerfile {
        from(javaBaseImage)
        copy(artifact, s"/app/${artifact.getName}")
        expose(ports: _*)
        entryPoint("java", "-DINTERFACE=0.0.0.0", s"-jar", s"app/${artifact.getName}")
      }
    },
    imageName(projectName)
  )
libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.6",
  "org.reactivemongo" %% "reactivemongo" % "1.0.7"
)

microService("vdb-services", 8093)
