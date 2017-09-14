//
//  AI.swift
//  Chess
//
//  Created by Jake Present on 6/19/16.
//  Copyright © 2016 Jake Present. All rights reserved.
//

import Foundation
import UIKit

class AIController {
    var level:Int //0 for human, 1-3 for AI
    let isWhite:Bool
    fileprivate let board:Board
    
    init(level:Int, isWhite:Bool, board:Board) {
        self.level = level
        self.isWhite = isWhite
        self.board = board
    }
    
    func move() {
        if level == 1 {
            levelOneMove()
        }
        else if level == 2 {
            levelTwoMove()
        }
        else if level == 3 {
            levelThreeMove()
        }
    }
    
    func levelOneMove() {
        var pos:RowCol
        var posList:[RowCol]
        var validPieces = [RowCol]()
        
        for r in 0...7 {
            for c in 0...7 {
                pos = RowCol(row: r, col: c)
                if let piece = board.getSpace(pos) as? Piece {
                    if piece.isWhite() == isWhite && board.possibleMoves(pos).count > 0 {
                        validPieces.append(pos)
                    }
                }
            }
        }
        
        if validPieces.count > 0 {
            pos = validPieces[Int(arc4random_uniform(UInt32(validPieces.count)))]
            board.selectPiece(pos)
            posList = board.possibleMoves(pos)
            
            let move = posList[Int(arc4random_uniform(UInt32(posList.count)))]
            board.movePiece(move)
        }
    }
    
    func levelTwoMove() {
        var validPieces = [RowCol]()
        var posList:[RowCol]
        var pos:RowCol
        var pieceToMove:RowCol?
        var placeToMove:RowCol?
        var highestValue = 0
        
        for r in 0...7 {
            for c in 0...7 {
                pos = RowCol(row: r, col: c)
                if let piece = board.getSpace(pos) as? Piece {
                    if piece.isWhite() == isWhite && board.possibleMoves(pos).count > 0 {
                        validPieces.append(pos)
                    }
                }
            }
        }
        
        if validPieces.count > 0 {
            for piece in validPieces {
                posList = board.possibleMoves(piece)
                for move in posList {
                    if board.getSpace(move).getValue() > highestValue {
                        highestValue = board.getSpace(move).getValue()
                        placeToMove = move
                        pieceToMove = piece
                    }
                }
            }
            
            if let move = pieceToMove {
                board.selectPiece(move)
                board.movePiece(placeToMove!)
            } else {
                levelOneMove()
            }
        }
    }
    
    func levelThreeMove() {
        var validPieces = [RowCol]()
        var threatenedPieces = [RowCol]()
        var posList:[RowCol]
        var attackedList:[RowCol]
        var pos: RowCol
        var pieceToMove: RowCol? = nil
        var placeToMove: RowCol? = nil
        var temp:Space
        var highestValue = 0
        var isTake = false
        
        for r in 0...7 {
            for c in 0...7 {
                
                pos = RowCol(row: r, col: c)
                
                if let piece = board.getSpace(pos) as? Piece {
                    if piece.isWhite() == isWhite && board.possibleMoves(pos).count > 0 {
                        validPieces.append(pos)
                        if board.isSquareAttacked(pos, isWhite: isWhite) && !(piece is King) {
                            threatenedPieces.append(pos)
                        }
                    }
                }
            }
        }
        
        if validPieces.count > 0 {
            for piece in validPieces { //takes a piece if it can
                
                posList = board.possibleMoves(piece)
                
                for move in posList {
                    
                    if board.getSpace(move).getValue() >= highestValue && board.getSpace(move).getValue() > 0 && (board.getSpace(move).getValue() >= board.getSpace(piece).getValue() || board.shouldTake(piece, attack: move)) {
                        highestValue = board.getSpace(move).getValue()
                        isTake = true
                        placeToMove = move
                        pieceToMove = piece
                    }
                    
                    if !(board.getSpace(piece) is King) && board.shouldTake(piece, attack: move) {
                        
                        temp = board.getSpace(move)
                        board.setSpace(move, space: board.getSpace(piece))
                        
                        attackedList = board.possibleMoves(move)
                        
                        if attackedList.count > 0 {
                            for attack in attackedList {
                                if board.getSpace(attack).getValue() > highestValue && (board.getSpace(attack).getValue() > board.getSpace(move).getValue() || !board.isSquareAttacked(attack, isWhite: isWhite)) {
                                    
                                    highestValue = board.getSpace(attack).getValue()
                                    isTake = false
                                    placeToMove = move
                                    pieceToMove = piece
                                }
                            }
                        }
                        board.setSpace(move, space: temp)
                    }
                }
            }
            
            if threatenedPieces.count > 0 {
                
                
                if isTake == false {
                    highestValue = 0
                }
                
                for piece in threatenedPieces {
                    if board.getSpace(piece).getValue() > highestValue {
                        
                        posList = board.possibleMoves(piece)
                        
                        for move in posList {
                            if board.isSquareAttacked(move, isWhite: isWhite) == false && board.getSpace(move).getValue() >= highestValue {
                                highestValue = board.getSpace(piece).getValue()
                                placeToMove = move
                                pieceToMove = piece
                            }
                        }
                    }
                }
            }
            
            if let piece = pieceToMove {
                board.selectPiece(piece)
                board.movePiece(placeToMove!)
                
            } else {
                levelOneMove()
            }
        }
    }
}



