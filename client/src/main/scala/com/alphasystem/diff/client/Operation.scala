package com.alphasystem.diff.client

import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait Operation extends EnumEntry

object Operation extends Enum[Operation] with CirceEnum[Operation] {
  override def values: IndexedSeq[Operation] = findValues

  final case object SES extends Operation

  final case object LCS extends Operation

}
