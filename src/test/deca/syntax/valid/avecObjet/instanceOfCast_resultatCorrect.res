`> [1, 0] Program
   +> ListDeclClass [List with 3 elements]
   |  []> [1, 0] DeclClass
   |  ||  +> [1, 6] Identifier (A)
   |  ||  +> [1, 8] Identifier (Object)
   |  ||  +> ListDeclField [List with 0 elements]
   |  ||  `> ListDeclMethod [List with 0 elements]
   |  []> [2, 0] DeclClass
   |  ||  +> [2, 6] Identifier (B)
   |  ||  +> [2, 8] Identifier (A)
   |  ||  +> ListDeclField [List with 0 elements]
   |  ||  `> ListDeclMethod [List with 0 elements]
   |  []> [3, 0] DeclClass
   |      +> [3, 6] Identifier (C)
   |      +> [3, 8] Identifier (B)
   |      +> ListDeclField [List with 0 elements]
   |      `> ListDeclMethod [List with 0 elements]
   `> [4, 0] Main
      +> ListDeclVar [List with 2 elements]
      |  []> [5, 2] DeclVar
      |  ||  +> [5, 0] Identifier (A)
      |  ||  +> [5, 2] Identifier (a)
      |  ||  `> NoInitialization
      |  []> [6, 2] DeclVar
      |      +> [6, 0] Identifier (B)
      |      +> [6, 2] Identifier (b)
      |      `> NoInitialization
      `> ListInst [List with 2 elements]
         []> [7, 2] Assign
         ||  +> [7, 0] Identifier (a)
         ||  `> [7, 4] New
         ||     `> [7, 8] Identifier (C)
         []> [8, 0] IfThenElse
             +> [8, 6] InstanceOf
             |  +> [8, 4] Identifier (a)
             |  `> [8, 17] Identifier (B)
             +> ListInst [List with 2 elements]
             |  []> [9, 2] Assign
             |  ||  +> [9, 0] Identifier (b)
             |  ||  `> [9, 4] Cast
             |  ||     +> [9, 5] Identifier (B)
             |  ||     `> [9, 8] Identifier (a)
             |  []> [10, 0] Println
             |      `> ListExpr [List with 1 elements]
             |         []> [10, 8] StringLiteral ("ok")
             `> ListInst [List with 0 elements]
