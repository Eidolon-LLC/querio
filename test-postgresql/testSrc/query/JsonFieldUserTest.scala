package query

import model.db.table.{MutableUser, User}
import org.json4s.JsonAST.{JField, _}
import org.json4s.jackson.JsonMethods
import test.{BaseScheme, DBUtil, DbTestBase}

class JsonFieldUserTest extends DbTestBase(
  crateSchemaSql = BaseScheme.crateSql,
  truncateSql = BaseScheme.truncateSql) {

  "Table \"user\"" should {

    "support simple access to json field" in new FreshDB {
      val user: MutableUser = DBUtil.dummyUser()
      val mail: String = "main@user.com"
      user.email = mail
      val expected: JValue = JsonMethods.parse("{}")
      user.js = expected

      db.dataTrReadCommittedNoLog {implicit dt =>
        db.insert(user)
      }

      val json = db.query(_.select(User.js)
        from User
        fetch()).head

      json must_== expected
    }

    "support simple access to jsonb field" in new FreshDB {
      val user: MutableUser = DBUtil.dummyUser()
      val mail: String = "main@user.com"
      user.email = mail
      val expected: JValue = JsonMethods.parse("{}")
      user.jsB = expected

      db.dataTrReadCommittedNoLog {implicit dt =>
        db.insert(user)
      }

      val json = db.query(_.select(User.jsB)
        from User
        fetch()).head

      json must_== expected
    }

    "support complex json " in new FreshDB {
      val user: MutableUser = DBUtil.dummyUser()
      val mail: String = "main@user.com"
      user.email = mail
      val expectedValue = 13
      val jsonInput: String = "{\"id\":13,\"name\":\"json name\",\"obj\":{\"arr\":[1,2,3],\"f\":122.34}}"
      val expected: JValue = JsonMethods.parse(jsonInput)
      user.js = expected

      db.dataTrReadCommittedNoLog {implicit dt =>
        db.insert(user)
      }

      val json = db.query(_.select(User.js)
        from User
        fetch()).head

      json must_== JObject(
        JField("id", JInt(13))
          :: JField("name", JString("json name"))
          :: JField("obj", JObject(
          JField("arr", JArray(List(JInt(1), JInt(2), JInt(3))))
            :: JField("f", JDouble(122.34))
            :: Nil
        ))
          :: Nil)
    }

    "support complex bjson " in new FreshDB {
      val user: MutableUser = DBUtil.dummyUser()
      val mail: String = "main@user.com"
      user.email = mail
      val expectedValue = 13
      val jsonInput: String = "{\"id\":13,\"name\":\"json name\",\"obj\":{\"arr\":[1,2,3],\"f\":122.34}}"
      val expected: JValue = JsonMethods.parse(jsonInput)
      user.js = expected

      db.dataTrReadCommittedNoLog {implicit dt =>
        db.insert(user)
      }

      val json = db.query(_.select(User.js)
        from User
        fetch()).head

      json must_== JObject(
        JField("id", JInt(13))
          :: JField("name", JString("json name"))
          :: JField("obj", JObject(
          JField("arr", JArray(List(JInt(1), JInt(2), JInt(3))))
            :: JField("f", JDouble(122.34))
            :: Nil
        ))
          :: Nil)
    }

  }
}
