//
//  Pieces.swift
//  Chess
//
//  Created by Jake Present on 6/15/16.
//  Copyright © 2016 Jake Present. All rights reserved.
//

import Foundation
import UIKit


class RowCol : NSObject, NSCoding {
    
    var row:Int
    var col:Int
    
    init (row:Int, col:Int) {
        self.row = row
        self.col = col
    }
    
    required init?(coder aDecoder: NSCoder) {
        row = aDecoder.decodeInteger(forKey: "row")
        col = aDecoder.decodeInteger(forKey: "col")
    }
    
    func encode(with aCoder: NSCoder) {
        
        aCoder.encode(row, forKey: "row")
        aCoder.encode(col, forKey: "col")
    }
    
    func equals (_ other:RowCol) -> Bool {
        if (row == other.row && col == other.col){
            return true
        }
        return false
    }
}

class EmptySpace : Space {
    init() {
        super.init(value: 0, type: .empty)
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
}

class Pawn : Piece {
    fileprivate var enPassantAble:Bool
    
    init(isWhite: Bool) {
        
        enPassantAble = false
        
        super.init(isWhite: isWhite, value: 1, type: .pawn, image: UIImage(named: "WhitePawn")!)
        if (!isWhite){
            self.setImage(UIImage(named: "BlackPawn")!)
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        
        enPassantAble = aDecoder.decodeBool(forKey: "enPassantAble")
        super.init(coder: aDecoder)
    }
    
    override func encode(with aCoder: NSCoder) {
        super.encode(with: aCoder)
        
        aCoder.encode(enPassantAble, forKey: "enPassantAble")
    }
    
    func isEnPassantAble() -> Bool{
        return enPassantAble
    }
    
    func setEnPassantAble(_ enPassantAble:Bool) {
        self.enPassantAble = enPassantAble
    }
}

class Bishop: Piece {
    init(isWhite:Bool){
        super.init(isWhite: isWhite, value: 3, type: .bishop, image: UIImage(named: "WhiteBishop")!)
        if (!isWhite){
            self.setImage(UIImage(named: "BlackBishop")!)
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
}

class Knight: Piece {
    init(isWhite:Bool){
        super.init(isWhite: isWhite, value: 3, type: .knight, image: UIImage(named: "WhiteKnight")!)
        if (!isWhite){
            self.setImage(UIImage(named: "BlackKnight")!)
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
}

class Rook: Piece {
    
    fileprivate var hasMoved:Bool
    
    init(isWhite: Bool) {
        hasMoved = false
        super.init(isWhite: isWhite, value: 5, type: .rook, image: UIImage(named: "WhiteRook")!)
        if (!isWhite){
            self.setImage(UIImage(named: "BlackRook")!)
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        hasMoved = aDecoder.decodeBool(forKey: "hasMoved")
        super.init(coder: aDecoder)
    }
    
    override func encode(with aCoder: NSCoder) {
        super.encode(with: aCoder)
        aCoder.encode(hasMoved, forKey: "hasMoved")
    }
    
    func move(){
        hasMoved = true
    }
    
    func getHasMoved() -> Bool {
        return hasMoved
    }
}

class Queen: Piece {
    init(isWhite:Bool){
        super.init(isWhite: isWhite, value: 9, type: .queen, image: UIImage(named: "WhiteQueen")!)
        if (!isWhite){
            self.setImage(UIImage(named: "BlackQueen")!)
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
}

class King: Piece {
    
    fileprivate var hasMoved:Bool
    
    init(isWhite: Bool) {
        hasMoved = false
        super.init(isWhite: isWhite, value: 10, type: .king, image: UIImage(named: "WhiteKing")!)
        if (!isWhite){
            self.setImage(UIImage(named: "BlackKing")!)
        }
    }
    
    required init?(coder aDecoder: NSCoder) {
        hasMoved = aDecoder.decodeBool(forKey: "hasMoved")
        super.init(coder: aDecoder)
    }
    
    override func encode(with aCoder: NSCoder) {
        super.encode(with: aCoder)
        aCoder.encode(hasMoved, forKey: "hasMoved")
    }
    
    func move(){
        hasMoved = true
    }
    
    func getHasMoved() -> Bool {
        return hasMoved
    }
}
