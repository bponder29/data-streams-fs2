case class Team
(
    id: Int,
    name: String,
    venue: Venue,
    abbreviation: String,
    teamName: String,
    locationName: String,
    firstYearOfPlay: String,
    division: Division,
    conference: Conference,
    franchise: Franchise,
    shortName: String,
    officialSiteUrl: String,
    franchiseId: Int,
    active: Boolean
)

case class Venue
(
    name: String,
    link: String,
    city: String,
    timeZone: TimeZone
)

case class TimeZone
(
    id: String,
    offset: Int,
    tz: String
)

case class Division
(
    id: Int,
    name: String,
    nameShort: String,
    link: String,
    abbreviation: String
)

case class Conference
(
    id: Int,
    name: String,
    link: String
)

case class Franchise
(
    franchiseId: Int,
    teamName: String,
    link: String
)

case class Copyright
(
  copyright: String
)