`> [1, 0] Program
   +> ListDeclClass [List with 1 elements]
   |  []> [1, 0] DeclClass
   |      +> [1, 6] Identifier (A)
   |      +> [1, 8] Identifier (Object)
   |      +> ListDeclField [List with 1 elements]
   |      |  []> [2, 8] DeclField
   |      |      PUBLIC
   |      |      +> [2, 4] Identifier (int)
   |      |      +> [2, 8] Identifier (a)
   |      |      `> NoInitialization
   |      `> ListDeclMethod [List with 0 elements]
   `> [4, 0] Main
      +> ListDeclVar [List with 1 elements]
      |  []> [5, 6] DeclVar
      |      +> [5, 4] Identifier (A)
      |      +> [5, 6] Identifier (class1)
      |      `> [5, 13] Initialization
      |         `> [5, 15] New
      |            `> [5, 19] Identifier (A)
      `> ListInst [List with 1 elements]
         []> [6, 4] IfThenElse
             +> [6, 14] NotEquals
             |  +> [6, 7] Identifier (class1)
             |  `> [6, 17] (null)
             +> ListInst [List with 1 elements]
             |  []> [7, 8] Return
             |      `> [7, 15] Int (3)
             `> ListInst [List with 0 elements]
