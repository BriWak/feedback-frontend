/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers

import config.FrontendAppConfig
import connectors.DataCacheConnector
import controllers.actions._
import forms.GiveCommentsFormProvider
import javax.inject.Inject
import navigation.Navigator
import pages.GenericQuestionsPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Action
import services.AuditService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import views.html.giveComments


class GiveCommentsController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        navigator: Navigator,
                                        formProvider: GiveCommentsFormProvider,
                                        auditService: AuditService
                                      ) extends FrontendController with I18nSupport {

  val form = formProvider()
  def submitCall(origin: String) = routes.GiveCommentsController.onSubmit(origin)

  def onPageLoad(origin: String) = Action {
    implicit request =>

      Ok(giveComments(appConfig, form, submitCall(origin)))
  }

  def onSubmit(origin: String) = Action {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          BadRequest(giveComments(appConfig, formWithErrors, submitCall(origin))),
        value => {

          auditService.giveCommentsAudit(origin, request.session.get("feedbackId").getOrElse("-"), value)
          Redirect(navigator.nextPage(GenericQuestionsPage)(()))
        }
      )
  }
}
