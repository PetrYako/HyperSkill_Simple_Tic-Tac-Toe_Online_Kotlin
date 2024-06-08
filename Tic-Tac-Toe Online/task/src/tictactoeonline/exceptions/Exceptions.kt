package tictactoeonline.exceptions

class AuthorizationException : Exception()

class GameJoinException : Exception()

class CreationException : Exception()

class ImpossibleMoveException : Exception()

class RegistrationException(msg: String) : Exception(msg)

class GetGameStatusException : Exception()

class NoRightException : Exception()