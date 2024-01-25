`> [11, 0] Program
   +> ListDeclClass [List with 0 elements]
   `> [11, 0] Main
      +> ListDeclVar [List with 4 elements]
      |  []> [12, 11] DeclVar
      |  ||  +> [12, 4] Identifier (String)
      |  ||  +> [12, 11] Identifier (a)
      |  ||  `> [12, 13] Initialization
      |  ||     `> [12, 15] StringLiteral ("anass")
      |  []> [13, 12] DeclVar
      |  ||  +> [13, 4] Identifier (boolean)
      |  ||  +> [13, 12] Identifier (t)
      |  ||  `> [13, 14] Initialization
      |  ||     `> [13, 16] BooleanLiteral (true)
      |  []> [14, 8] DeclVar
      |  ||  +> [14, 4] Identifier (int)
      |  ||  +> [14, 8] Identifier (n)
      |  ||  `> [14, 10] Initialization
      |  ||     `> [14, 12] Int (3)
      |  []> [15, 8] DeclVar
      |      +> [15, 4] Identifier (int)
      |      +> [15, 8] Identifier (m)
      |      `> NoInitialization
      `> ListInst [List with 2 elements]
         []> [16, 4] IfThenElse
         ||  +> [16, 21] Or
         ||  |  +> [16, 11] And
         ||  |  |  +> [16, 9] Identifier (t)
         ||  |  |  `> [16, 16] Greater
         ||  |  |     +> [16, 14] Identifier (n)
         ||  |  |     `> [16, 18] Int (0)
         ||  |  `> [16, 24] BooleanLiteral (true)
         ||  +> ListInst [List with 3 elements]
         ||  |  []> [17, 10] Assign
         ||  |  ||  +> [17, 8] Identifier (m)
         ||  |  ||  `> [17, 12] Int (5)
         ||  |  []> [18, 8] Print
         ||  |  ||  `> ListExpr [List with 1 elements]
         ||  |  ||     []> [18, 14] StringLiteral ("n est positif")
         ||  |  []> [19, 8] Println
         ||  |      `> ListExpr [List with 1 elements]
         ||  |         []> [19, 16] StringLiteral ("m = 5")
         ||  `> ListInst [List with 1 elements]
         ||     []> [21, 12] IfThenElse
         ||         +> [21, 14] Lower
         ||         |  +> [21, 12] Identifier (n)
         ||         |  `> [21, 16] Int (0)
         ||         +> ListInst [List with 4 elements]
         ||         |  []> [22, 8] While
         ||         |  ||  +> [22, 14] Identifier (t)
         ||         |  ||  `> ListInst [List with 2 elements]
         ||         |  ||     []> [23, 14] Assign
         ||         |  ||     ||  +> [23, 12] Identifier (m)
         ||         |  ||     ||  `> [23, 16] Identifier (n)
         ||         |  ||     []> [24, 14] Assign
         ||         |  ||         +> [24, 12] Identifier (n)
         ||         |  ||         `> [24, 17] Plus
         ||         |  ||            +> [24, 16] Identifier (n)
         ||         |  ||            `> [24, 18] Int (1)
         ||         |  []> [26, 10] Assign
         ||         |  ||  +> [26, 8] Identifier (m)
         ||         |  ||  `> [26, 12] UnaryMinus
         ||         |  ||     `> [26, 13] Int (5)
         ||         |  []> [27, 8] Print
         ||         |  ||  `> ListExpr [List with 1 elements]
         ||         |  ||     []> [27, 14] StringLiteral ("n est négatif")
         ||         |  []> [28, 8] Print
         ||         |      `> ListExpr [List with 1 elements]
         ||         |         []> [28, 14] StringLiteral ("m = -5")
         ||         `> ListInst [List with 3 elements]
         ||            []> [31, 12] Print
         ||            ||  `> ListExpr [List with 1 elements]
         ||            ||     []> [31, 18] StringLiteral ("n est nulle")
         ||            []> [32, 12] Println
         ||            ||  `> ListExpr [List with 1 elements]
         ||            ||     []> [32, 20] StringLiteral ("fin de if_then_else")
         ||            []> [33, 14] Assign
         ||                +> [33, 12] Identifier (m)
         ||                `> [33, 16] Int (0)
         []> [35, 4] While
             +> [35, 24] Or
             |  +> [35, 13] And
             |  |  +> [35, 11] Identifier (t)
             |  |  `> [35, 18] GreaterOrEqual
             |  |     +> [35, 16] Identifier (n)
             |  |     `> [35, 21] Int (0)
             |  `> [35, 27] BooleanLiteral (true)
             `> ListInst [List with 2 elements]
                []> [36, 10] Assign
                ||  +> [36, 8] Identifier (m)
                ||  `> [36, 14] Plus
                ||     +> [36, 12] Identifier (m)
                ||     `> [36, 16] Int (1)
                []> [37, 8] IfThenElse
                    +> [37, 14] Equals
                    |  +> [37, 12] Identifier (m)
                    |  `> [37, 16] Int (0)
                    +> ListInst [List with 2 elements]
                    |  []> [38, 12] Print
                    |  ||  `> ListExpr [List with 1 elements]
                    |  ||     []> [38, 18] StringLiteral ("m est nulle")
                    |  []> [39, 12] Println
                    |      `> ListExpr [List with 1 elements]
                    |         []> [39, 20] StringLiteral ("if1")
                    `> ListInst [List with 1 elements]
                       []> [41, 16] IfThenElse
                           +> [41, 18] Equals
                           |  +> [41, 16] Identifier (m)
                           |  `> [41, 20] Int (1)
                           +> ListInst [List with 2 elements]
                           |  []> [42, 12] Print
                           |  ||  `> ListExpr [List with 1 elements]
                           |  ||     []> [42, 18] StringLiteral ("m = 1")
                           |  []> [43, 12] Println
                           |      `> ListExpr [List with 1 elements]
                           |         []> [43, 20] StringLiteral ("if2")
                           `> ListInst [List with 2 elements]
                              []> [46, 12] Print
                              ||  `> ListExpr [List with 1 elements]
                              ||     []> [46, 19] StringLiteral ("m != 0 & m!= 1")
                              []> [47, 12] Println
                                  `> ListExpr [List with 1 elements]
                                     []> [47, 21] StringLiteral ("else")
