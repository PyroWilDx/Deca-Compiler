`> [11, 0] Program
   +> ListDeclClass [List with 1 elements]
   |  []> [11, 0] DeclClass
   |      +> [11, 6] Identifier (A)
   |      +> [11, 8] Identifier (Object)
   |      +> ListDeclField [List with 1 elements]
   |      |  []> [12, 8] DeclField
   |      |      PUBLIC
   |      |      +> [12, 4] Identifier (int)
   |      |      +> [12, 8] Identifier (a)
   |      |      `> Initialization
   |      |         `> [12, 12] Int (2)
   |      `> ListDeclMethod [List with 2 elements]
   |         []> [13, 4] DeclMethod
   |         ||  index : 0
   |         ||  +> [13, 4] Identifier (int)
   |         ||  +> [13, 8] Identifier (summ)
   |         ||  +> [13, 13] ListDeclParam [List with 1 elements]
   |         ||  |  []> [13, 13] DeclParam
   |         ||  |      +> [13, 13] Identifier (int)
   |         ||  |      `> [13, 17] Identifier (b)
   |         ||  +> ListDeclVar [List with 0 elements]
   |         ||  `> ListInst [List with 1 elements]
   |         ||     []> [14, 8] Return
   |         ||         `> [14, 19] FieldSelection
   |         ||            +> [14, 15] This
   |         ||            `> [14, 20] Identifier (a)
   |         []> [16, 4] DeclMethod
   |             index : 0
   |             +> [16, 4] Identifier (int)
   |             +> [16, 8] Identifier (summ2)
   |             +> [16, 14] ListDeclParam [List with 0 elements]
   |             +> ListDeclVar [List with 0 elements]
   |             `> ListInst [List with 1 elements]
   |                []> [17, 8] Return
   |                    `> [17, 24] MethodCall
   |                       +> [17, 15] This
   |                       +> [17, 20] Identifier (summ)
   |                       `> [17, 25] RValueStar [List with 1 elements]
   |                          []> [17, 25] Int (5)
   `> [20, 0] Main
      +> ListDeclVar [List with 3 elements]
      |  []> [21, 6] DeclVar
      |  ||  +> [21, 4] Identifier (A)
      |  ||  +> [21, 6] Identifier (anass)
      |  ||  `> [21, 12] Initialization
      |  ||     `> [21, 14] New
      |  ||        `> [21, 18] Identifier (A)
      |  []> [22, 8] DeclVar
      |  ||  +> [22, 4] Identifier (int)
      |  ||  +> [22, 8] Identifier (c)
      |  ||  `> [22, 10] Initialization
      |  ||     `> [22, 22] MethodCall
      |  ||        +> [22, 12] Identifier (anass)
      |  ||        +> [22, 18] Identifier (summ)
      |  ||        `> [22, 23] RValueStar [List with 1 elements]
      |  ||           []> [22, 23] Int (5)
      |  []> [23, 8] DeclVar
      |      +> [23, 4] Identifier (int)
      |      +> [23, 8] Identifier (d)
      |      `> [23, 10] Initialization
      |         `> [23, 23] MethodCall
      |            +> [23, 12] Identifier (anass)
      |            +> [23, 18] Identifier (summ2)
      |            `> [23, 24] RValueStar [List with 0 elements]
      `> ListInst [List with 0 elements]
