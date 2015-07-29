import com.googlecode.kanbanik.dtos.ErrorDto
import com.googlecode.kanbanik.model.User
import net.liftweb.json._

case class WithAuth(user: User) {
  def canExecute(command: ExpCommand, param: String): Boolean = {
    print("executing for the firs")
    true
  }

  def canExecute(command: ExpCommand2, param: Int): Boolean = {
    print(2)
    print("executing for the second")
    true
  }

  def canExecute(command: ExpCommand3, param: Int): Boolean = true

  def canExecute[T, R](command: Command2[T, R], param: T): Boolean = {
    val cmdClass = command.getClass
    val paramClass = param.getClass
    print("the WithAuth.canExecute for type " + command.getClass + " is not defined!")
    throw new UnsupportedOperationException(s"the WithAuth.canExecute for type $cmdClass with params $paramClass is not defined!")
  }
}

implicit def toAuth(user: User) = WithAuth(user)


abstract class Command2[T: Manifest, R] {

  implicit val formats = DefaultFormats

  def execute(parsedJson: JValue, user: User): Either[R, ErrorDto] = {
    val param: T = parsedJson.extract[T]

    user.canExecute[T, R](this, param)

    execute(param, user)
  }

  def execute(dto: T, user: User) : Either[R, ErrorDto] = {
    execute(dto)
  }

  def execute(dto: T) : Either[R, ErrorDto]
}


case class ExpCommand() extends Command2[String, String] {
  override def execute(dto: String): Either[String, ErrorDto] = ???


}

case class ExpCommand2() extends Command2[Int, Int] {
  override def execute(dto: Int): Either[Int, ErrorDto] = ???
}

case class ExpCommand3() extends Command2[String, String] {
  override def execute(dto: String): Either[String, ErrorDto] = ???
}

val u = User()

val c = ExpCommand()
val c2 = ExpCommand2()
val c3 = ExpCommand3()

val x = 21
val s = s"something $x.toDouble"


//u.canExecute(c2)
//u.canExecute(c)
//u.canExecute(c3)




