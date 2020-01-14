name := "data-stream-fs2"
version := "0.1"
scalaVersion := "2.12.10"

scalacOptions += "-Ypartial-unification"

val Fs2Version        = "2.1.0"
val Http4sVersion     = "0.21.0-SNAPSHOT"
val CirceVersion      = "0.12.3"
val DoobieVersion     = "0.8.8"
val ZIOVersion        = "1.0.0-RC17"
val PureConfigVersion = "0.11.0"
val H2Version         = "1.4.199"
val FlywayVersion     = "6.0.0-beta2"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "dev.zio"                      %% "zio"                           % ZIOVersion,
  "dev.zio"                      %% "zio-interop-cats"              % "2.0.0.0-RC10",
  "dev.zio"                      %% "zio-test"                      % ZIOVersion % "test",
  "dev.zio"                      %% "zio-test-sbt"                  % ZIOVersion % "test",
  "org.http4s"                   %% "http4s-blaze-server"           % Http4sVersion,
  "org.http4s"                   %% "http4s-blaze-client"           % Http4sVersion,
  "org.http4s"                   %% "http4s-circe"                  % Http4sVersion,
  "org.http4s"                   %% "http4s-dsl"                    % Http4sVersion,
  "io.circe"                     %% "circe-generic"                 % CirceVersion,
  "org.tpolecat"                 %% "doobie-core"                   % DoobieVersion,
  "org.tpolecat"                 %% "doobie-h2"                     % DoobieVersion,
  "com.github.pureconfig"        %% "pureconfig"                    % PureConfigVersion,
  "com.h2database"               % "h2"                             % H2Version,
  "net.logstash.logback"         % "logstash-logback-encoder"       % "5.3",
  "ch.qos.logback"               % "logback-classic"                % "1.2.3",
  "co.fs2"                       %% "fs2-core"                      % Fs2Version,
  "co.fs2"                       %% "fs2-io"                        % Fs2Version,
  "co.fs2"                       %% "fs2-reactive-streams"          % Fs2Version
)
