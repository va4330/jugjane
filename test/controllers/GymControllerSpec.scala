package controllers

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.test.FakeRequest
import play.api.test.Helpers._
import models.domain.services.{PhotoServiceComponent, RouteServiceComponent, AuthServiceComponent, GymServiceComponent}
import models.domain.gym.Demo
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import org.specs2.specification.Scope
import scala.util.Success
import test.TestUtils
import models.data.dao.RouteDaoComponent
import models.domain.services.impl.PhotoServiceComponentImpl

class GymControllerSpec extends Specification with Mockito {
  "newBoulder" should {
    "succeed for existing gym" in new GymControllerScope {
      // Setup
      gymService.get("demo").returns(Success(Demo))

      val result = newBoulder("demo")(FakeRequest(GET, "/climbing/demo/new"))

      // Assert
      status(result) must equalTo(200)

      // Verfy
      there was one(gymService).get("demo")
    }
  }

  "get" should {
    "succeed for existing gym" in new GymControllerScope {
      // Setup
      gymService.get("demo") returns Success(Demo)
      routeService.getByGymHandle("demo") returns Future(Nil)
      authService.isAdmin(any, any) returns Success(false)

      val result = get("demo", None)(FakeRequest(GET, "/climbing/demo"))

      // Assert
      status(result) must equalTo(200)

      // Verfy
      there was one(gymService).get("demo")
      there was one(routeService).getByGymHandle("demo")
      there was no(authService).validateSecret(any, any)
    }

    "set cookie for correct secret" in new GymControllerScope {
      // Setup
      gymService.get("demo") returns Success(Demo)
      routeService.getByGymHandle("demo") returns Future(Nil)
      authService.validateSecret("123", "demo") returns Success(true)

      val result = get("demo", Some("123"))(FakeRequest(GET, "/climbing/demo"))

      // Assert
      status(result) must equalTo(200)
      cookies(result) must not beEmpty

      // Verfy
      there was one(gymService).get("demo")
      there was one(routeService).getByGymHandle("demo")
      there was one(authService).validateSecret("123", "demo")
    }

    "not set cookie for incorrect secret" in new GymControllerScope {
      // Setup
      gymService.get("demo") returns Success(Demo)
      routeService.getByGymHandle("demo") returns Future(Nil)
      authService.isAdmin(any, any) returns Success(false)
      authService.validateSecret("123", "demo") returns Success(false)

      val result = get("demo", Some("123"))(FakeRequest(GET, "/climbing/demo"))

      // Assert
      status(result) must equalTo(200)
      cookies(result) must beEmpty

      // Verfy
      there was one(gymService).get("demo")
      there was one(routeService).getByGymHandle("demo")
      there was one(authService).validateSecret("123", "demo")
    }
  }

  trait GymControllerScope extends Scope with GymController
    with GymServiceComponent with AuthServiceComponent with RouteServiceComponent
    with PhotoServiceComponent {
    val gymService: GymService = mock[GymService]
    val authService: AuthService = mock[AuthService]
    val routeService: RouteService = mock[RouteService]
    val photoService: PhotoService = mock[PhotoService]
  }
}
