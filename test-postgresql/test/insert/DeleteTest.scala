package insert

import java.time.LocalDateTime

import model.db.table.{MutableUser, User}
import querio.ModifyData
import test.{BaseScheme, DBUtil, DbTestBase}

class DeleteTest extends DbTestBase(
  crateSchemaSql = BaseScheme.crateSql,
  truncateSql = BaseScheme.truncateSql) {

  val mddt = new ModifyData {
    override def dateTime: LocalDateTime = LocalDateTime.now()
  }

  "Table \"user\"" should {

    "show no effect if data is absent" in new FreshDB{
      val result1 = db.query(_.select(User.email)
        from User
        limit 10
        fetch())
      result1 must beEmpty
      //
      val user: MutableUser = DBUtil.dummyUser()
      val mail: String = "main@user.com"
      user.email = mail
      val result2 = db.dataTrReadCommittedNoLog {implicit dt =>
        db.delete(User, 0)
      }
      result2 must_== 0
    }

    "remove only requested data" in new FreshDB{
      val result1 = db.query(_.select(User.email)
        from User
        limit 10
        fetch())
      result1 must beEmpty
      //
      val mails = Range(1, 4).map {index =>
        s"main$index@user.com"
      }
      val users = mails.map {mail =>
        val user: MutableUser = DBUtil.dummyUser()
        user.email = mail
        user
      }
      db.dataTrReadCommitted(mddt) {implicit dt =>
        users.foreach {user =>
          db.insert(user)
        }
      }

      //
      val result2 = db.query(_.select(User.email,User.id)
        from User
        limit 10
        fetch())
      result2.map(_._1) sameElements mails

      db.dataTrReadCommitted(mddt) {implicit dt =>
        db.delete(User, result2.head._2)
      } must_== 1

      val result3 = db.query(_.select(User.email,User.id)
        from User
        limit 10
        fetch())
      result3 sameElements result2.tail
    }
  }
}