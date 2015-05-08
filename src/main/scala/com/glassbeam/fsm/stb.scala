package com.glassbeam.fsm

import scala.tools.reflect.ToolBox
import scala.collection.mutable.ListBuffer
import akka.actor.Actor
import akka.actor.ActorRef
/*
 * this code creates an actor from the rules definition 
 * we invoke the actor by sending a msg 
 */
object stb extends App {

  val tb = scala.reflect.runtime.universe.runtimeMirror(getClass.getClassLoader).mkToolBox()

  val actorName = "A"

  val col0 = """
    val x = ListBuffer[Int]()
    val y = ListBuffer[Int]()
    """

  val col1 = """
    x += 2
    y += 3
    """

  val ruleTemplate = s"""
    import scala.collection.mutable.ListBuffer
    import akka.actor.Actor
    import akka.actor.Props

    class ${actorName} extends Actor {
    
    import context._ 
    
    ${col0}
    
    def receive = {
      case "start" =>
      println("start and becoming col1") 
      become(col1)
    }
    
    def col1:Receive = {
      case "event" => ${col1}
    }
    
    }
    
    
    import akka.actor.ActorSystem
    val system = ActorSystem("mySystem")
    val myActor = system.actorOf(Props[${actorName}], "${actorName}")
    myActor    
    """

  // printing the generated code for the RuleActor 
  println(ruleTemplate)

  // Iam using scala toolbox to parse the ruleTemplate
  val tree = tb.parse(ruleTemplate)

  // Iam casting the dynmically created actor as ActorRef  
  val actor: ActorRef = tb.eval(tree).asInstanceOf[ActorRef]
  
  // for demo purpose Iam just calling the actor 
  actor ! "start"
}