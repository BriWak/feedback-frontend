/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package navigation

import controllers.routes
import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import pages._
import models._

trait NextPage[A, B] {

  def nextPage(page: A): B => Call
}

object NextPage {

  implicit val genericQuestionsWithOriginNextPage: NextPage[GenericQuestionsPage.type, Origin] =
    new NextPage[GenericQuestionsPage.type, Origin] {
      override def nextPage(page: GenericQuestionsPage.type): Origin => Call =
        origin => controllers.routes.ThankYouController.onPageLoadWithOrigin(origin)
    }

  implicit val genericQuestionsNextPage: NextPage[GenericQuestionsPage.type, Unit] =
    new NextPage[GenericQuestionsPage.type, Unit] {
      override def nextPage(page: GenericQuestionsPage.type): Unit => Call =
        _ => controllers.routes.ThankYouController.onPageLoad()
    }

  implicit val pensionQuestionsNextPage: NextPage[PensionQuestionsPage.type, Unit] =
    new NextPage[PensionQuestionsPage.type, Unit] {
      override def nextPage(page: PensionQuestionsPage.type): Unit => Call =
        _ => controllers.routes.ThankYouController.onPageLoadPension()
    }

}

@Singleton
class Navigator @Inject()() {

  def nextPage[A, B](page: A)(b: B)(implicit np: NextPage[A, B]): Call = np.nextPage(page)(b)
}

@Singleton
class EothoNavigator @Inject()() {
  private val normalRoutes: Page => UserAnswers => Call = {
    case EothoNumberOfEstablishmentsPage =>
      _ =>
        routes.CheckYourAnswersController.onPageLoad()
    case _ =>
      _ =>
        routes.EothoNumberOfEstablishmentsController.onPageLoad(NormalMode)
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case _ =>
      _ =>
        routes.CheckYourAnswersController.onPageLoad()
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
