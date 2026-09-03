Changelog
=========

Unreleased
----------

* Add Bitwuzla solver support
* Fix interpreter handling of complex executable names with arguments
* Add continuous integration (CI) pipeline
* Update to Scala 3.7.0
* Update sbt to 1.11.1
* Add floating point theory support
* Support for cvc5 solver
* Upgrade to Scala 3.3
* Make some arithmetic operations n-ary
* Add int2bv and bv2nat operations
* Read stderr and return error if not empty
* Cross-compilation for Scala 3.0
* Add lexing for #unspecified literal (Z3 4.8.10)
* Remove bintray settings
* Make model token optional when parsing solver output
* Fix SimpleTreeTransformer
* Fix or operation
* Minor changes and upgrade to Scala 2.13.1
* Remove hack for CVC4 and add -i flag as default
* Various CVC4 compatibility fixes

v0.2.4
------
*Released 20 June 2019*

* Cross-compile to 2.13
* Update sbt to 1.2.8
* Fix a few bugs affecting CVC4

v0.2.3
------
*Released 18 December 2017*

* Fix BitVectorConstant extraction
* Make sure positions are always set
* New architecture for tree transformers
* Automatic Git-based versioning scheme

v0.2.1
---------------------------
*Released 28 December 2016*

* Cross-compile to 2.12
* Deployment to Maven Sonatype
* Finished TIP parser
* TIP extensions
* Tree Transformers API
* Made Interpreter interface a bit easier to use
* Hexadecimal parsing from any int type
* Remove erroneous symbol characters

v0.2
-----------------------
*Released 9 March 2016*

* Constructors and extractors for the standard theories (except Floating point) of SMT-LIB 2.5
  * Experimental support for non-standardized theories such as Strings and Sets.
* More robust parser and printers.
* Bug fixes, mostly small edge cases and weird symbol names.


v0.1
-----------------------
*Released 2 April 2015*

Initial version of Scala SmtLib.

* Extensive support for the SMT-LIB language version 2.5
* SMT-LIB expressions represented with a Scala AST
  * Parser turn input stream into AST representation
  * Printer turn AST representation into an SMT-LIB complient string output
* abstraction layer to communicate with solver processes over their textual input 
  * Support for Z3 and CVC4
* Scala SMT-LIB is released under the terms of the MIT license.

