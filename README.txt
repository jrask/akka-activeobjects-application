
[ AkkaSports - Demo app]

Application that keep track of matches (so far), including results, comments and score.

Features

 * Implemented with Java actors (ActiveObject)
 
 * Akka-spring is used to configure actors

 * Match feeds are imported using akka-camel.
   
 * Matches are persisted using akka-persistence-resin.
 
 * Akka supervision features are used to supervise parsers
   and matches.
   
Status

 Import
 - Camel is used. Works fine but will be extended.
 - I have not figured out how to correctly invoke the jetty endpoint 
   with a separate program, but I assume this is really easy.
 
 Parsing
 - How many parsers (actors) that should be used is configurable
 - Currently an internal ugly format is used but this will be extended with json and perhaps xml.
 - Parsers create Events of different types

 Match state
 - Match actor is transactional
 - Match state persisted in redis database
 - On postRestart is state refreshed with database
 - State is stored but it is not recovered after the application is stopped.
   Must add so it reads all matches and starts actors on startup
 
App Bugs

 - Due to something(!?), the app sometimes fails to figure out weather a match exists
   or not when looking in the PersistentMap. 
   first the map.get(KEY) returns Some, but a few nanoseconds later it returns None?! 
   The code below throws a NoSuchMethodException None.get() when trying to throw the RTE.
   if(hasKey(KEY_HOME)) {
	   throw new RuntimeException("ALREADY EXISTS:" + uid + ", home = " + get(KEY_HOME));				
   }
   Seems like a bug but I am not sure...
   I will try to isolate the problem in a test case.
   
 - Supervision on MatchActors is disabled since it does not work when I choose to use
   an interface.
   
 - MatchActors are not created on the same thread as the object invoking and using the
   actor which causes strange behaviour. Will figure out how to solve this much better.
   
Supervision

 - The spring supervision config available was not suitable when dealing with many actors
   and actors that are added dynamically, in this case prototype beans. Currently I am using
   a BeanPostProcessor to link actors to a supervisor. Since I do not know if I can hook in to 
   shutdown method I do not know if I can unlink a supervisor.
   
Testing
 - Current automatic tests are mostly invoked on beans from the appcontext since I know that they
   are Actors and async. Difficult to test this with mocking but perhaps some mock guru can help
   me out to improve tests. It important that we use good test examples as well since this can
   be hard. Perhaps we can make use of Johan and Jans framwork for async testing.
 