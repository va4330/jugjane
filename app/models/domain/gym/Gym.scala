package models.domain.gym

import models.domain.grade.GradingSystem
import models.domain.grade.Grade
import models.domain.grade.Discipline.Discipline
import java.net.URI
import java.net.URL
import models.domain.route.Tag
import java.util.Locale

trait Gym {
  def name: String
  def url: URL
  def handle: String
  def secret: String
  def gradingSystem: GradingSystem[Grade]
  def disciplines: Set[Discipline]
  def holdColors: Set[ColoredHolds]
  def tags: List[Tag]
  def address: Address
}

case class Address(
  val street: String,
  val city: String,
  val country: Locale)