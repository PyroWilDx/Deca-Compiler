`> [11, 0] Program
   +> ListDeclClass [List with 2 elements]
   |  []> [11, 0] DeclClass
   |  ||  +> [11, 6] Identifier (A)
   |  ||  +> [11, 8] Identifier (Object)
   |  ||  +> ListDeclField [List with 1 elements]
   |  ||  |  []> [12, 8] DeclField
   |  ||  |      PUBLIC
   |  ||  |      +> [12, 4] Identifier (int)
   |  ||  |      +> [12, 8] Identifier (a)
   |  ||  |      `> NoInitialization
   |  ||  `> ListDeclMethod [List with 1 elements]
   |  ||     []> [13, 4] DeclMethod
   |  ||         index : 0
   |  ||         +> [13, 4] Identifier (int)
   |  ||         +> [13, 8] Identifier (summ)
   |  ||         +> [13, 13] ListDeclParam [List with 2 elements]
   |  ||         |  []> [13, 13] DeclParam
   |  ||         |  ||  +> [13, 13] Identifier (int)
   |  ||         |  ||  `> [13, 17] Identifier (b)
   |  ||         |  []> [13, 19] DeclParam
   |  ||         |      +> [13, 19] Identifier (int)
   |  ||         |      `> [13, 23] Identifier (c)
   |  ||         +> ListDeclVar [List with 0 elements]
   |  ||         `> ListInst [List with 2 elements]
   |  ||            []> [14, 8] Print
   |  ||            ||  `> ListExpr [List with 1 elements]
   |  ||            ||     []> [14, 15] Plus
   |  ||            ||         +> [14, 14] Identifier (b)
   |  ||            ||         `> [14, 16] Int (1)
   |  ||            []> [15, 8] Return
   |  ||                `> [15, 15] Identifier (a)
   |  []> [18, 0] DeclClass
   |      +> [18, 6] Identifier (B)
   |      +> [18, 8] Identifier (A)
   |      +> ListDeclField [List with 1 elements]
   |      |  []> [19, 8] DeclField
   |      |      PUBLIC
   |      |      +> [19, 4] Identifier (int)
   |      |      +> [19, 8] Identifier (b)
   |      |      `> NoInitialization
   |      `> ListDeclMethod [List with 0 elements]
   `> [21, 0] Main
      +> ListDeclVar [List with 1 elements]
      |  []> [22, 6] DeclVar
      |      +> [22, 4] Identifier (A)
      |      +> [22, 6] Identifier (anass)
      |      `> [22, 12] Initialization
      |         `> [22, 14] New
      |            `> [22, 18] Identifier (A)
      `> ListInst [List with 2 elements]
         []> [23, 12] Assign
         ||  +> [23, 9] FieldSelection
         ||  |  +> [23, 4] Identifier (anass)
         ||  |  `> [23, 10] Identifier (a)
         ||  `> [23, 14] Int (1)
         []> [24, 14] MethodCall
             +> [24, 4] Identifier (anass)
             +> [24, 10] Identifier (summ)
             `> [24, 15] RValueStar [List with 2 elements]
                []> [24, 15] Identifier (a)
                []> [24, 18] Identifier (b)
