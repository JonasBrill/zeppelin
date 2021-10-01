# Apache Zeppelin, das Dokumenatationstool, für ArAMIS

----------

## Requirements

- Java 1.8
- maven 3.6.3 oder höher
- Docker
- git
- Linux oder MacOS (WSL reicht aus)

## Developement

1. Projekt clonen (falls WSL benutzt wird, sollte das Projekt in ein Unterverzeichnis des WSL-Homeverzeichnisses geclont werden).
2. Das Projekt mit `mvn clean package -DskipTests` bauen.
3. Bei Änderungen am Code:
3.1 Sollte nur das neo4jhttp Plugin angepasst werden, so reicht der Befehl `mvn clean package -pl neo4jhttp -am -DskipTests` aus.
3.2 Werden Änderungen an anderen Plugins, dem Frontende o.ä. vorgenommen muss das gesammte Projekt neu gebaut werden.
4. Gestartet wird das Projekt durch `. zeppelin-deamon.sh start` im *bin* Verzeichnis.

## Production

1. Der Befehl `mvn clean package -Pbuild-distr` erzeucht eine tgz Datei, die vor dem Docker build Schritt erzeugt werden muss und anschliesßend ind das Verzeichnis *scripts\docker\zeppelin\bin* kopiert werden muss.
2. Im Verzeichnis *scripts\docker\zeppelin\bin* befindet sich ein Dockerscript, welches durch `docker build -t example/example:example ./` das Dockerimage für Zeppelin baut.

## Weitere Hinweise

- Für die Verbindung zur Datenbank muss in Neo4j bei der verwendeten Datenbank http als Protokoll erlaubt werden.
- Im neo4jhttp Ordner befinden sich neben dem Interpreter auch Datein für beispielsweise Syntaxhighlighting. Dies sind Dateien, die vom Ace-Editor verstanden bzw. genutzt werden. Eine genauere DOku dazu ist unter follgender URL zu finden: [Ace-Editor](https://ace.c9.io/)
- Apache Zeppelin stellt eine API bereit um beispielsweise Notebooks zu erstellen oder EInstellungen zu konfigurieren. Eine Dokumentation dazu befindet sich hier : [Apache Zeppelin API Doku](https://zeppelin.apache.org/docs/0.10.0/usage/rest_api/notebook.html).
- Die meisten Infos zu Apache Zeppelin und den verschiedensten Konfigurationsmöglichkeiten können unter [Apche Zeppelin](https://zeppelin.apache.org/docs/0.10.0/) gefunden werden.
