JC =javac
.SUFFIXES:.java .class
.java.class: 
	$(JC) $*.java
RM = rm -f

CLASSES = TestManager.java LoginHandler.java QuestionHandler.java UserInfo.java Communication.java CheckCodingHandler.java LastPageHandler.java QuestionGenerator.java

default: classes

classes:$(CLASSES:.java=.class)

clean:\
	$(RM) *.class