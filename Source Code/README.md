# Source Code
> Language used: Java

## Capabilities Class
Contains the names of possible actions along with their current permission. This also includes methods to check the current capabilities allowed and modify the capabilities

## ACM Class
> Abstract  

Contains data that is generic to any ACM. This includes subjects, objects, capabilities, log data, and methods to view the data in the ACM

## ACMUse Class
A class to contain log information for the ACM. This tracks the users requesting certain actions, whether it was granted, and the effects caused by the action. This also includes a method to view the log entry

## EditorialACM Class
> Inherits ACM  

Contains data specific to the Editorial Managment system and implements it as an ACM. This includes the methods to check actions for each user in the system