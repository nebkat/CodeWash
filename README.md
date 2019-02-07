# CodeWash

Welcome to the CodeWash

# Code Smells
* [Bloated Code](#bloated-code)
   - [Long Methods](#long-methods)
   - [Long Variable Names](#long-variable-names)
   - [Long Classes](#long-classes)
   - [Arrowhead Indentation](#arrowhead-indentation)
   - [Switch Statements](#switch-statements)
* [OOP Violations](#oop-violations)
   - [Temporary Fields](#temporary-fields)
   - [Violations of Data Hiding](#violations-of-data-hiding)
* [Feature Envy](#feature-envy)
   - [Excessive Use of Other Classes](#excessive-use-of-other-classes)

### Bloated Code
To do too much or too little?

#### Long Methods

#### Long Variable Names

#### Long Classes

#### Arrowhead Indentation
Overcomplicated logic in code can often be seen in the shape of an arrowhead. Overly complicated logic makes code harder to understand and maintain so keeping logic conditions concise and clear is really important to writing good code.

#### Switch Statements
While switch statements do have their place in progamming often they can lead duplicated code scattered throughout the program. The OOP concept of Polymorphism has many advantages, one being when the same set of conditions appear across the program. Updating these conditions can be troublesome if you want to use a different/new type, so using polymorphism reduces dependencies. **Most of the time when you see a switch statement you should consider polymorphism.**

#### Long Methods

### OOP Violations

#### Temporary Fields
Local variables that are specific to a method are defined at a class level.

#### Violations of Data Hiding

#### Refused Bequest
A class overrides an inherited method in a way that undermines it's functionality.

### Feature Envy

#### Excessive Use of Other Classes
