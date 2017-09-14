//
//  Board.swift
//  Chess
//
//  Created by Jake Present on 6/17/16.
//  Copyright © 2016 Jake Present. All rights reserved.
//

import Foundation
import UIKit

class Board: NSObject, NSCoding {
    
    fileprivate var board = [[Space]](repeating: [Space](repeating: EmptySpace(), count: 8), count: 8)
    fileprivate let WHITE = true
    fileprivate let BLACK = false
    
    var whiteCheck = false
    var blackCheck = false
    
    var playerTurn = true
    
    var whiteScore = 0
    var blackScore = 0
    var whiteWins = false
    var blackWins = false
    var stalemate = false
    var draw = false
    var threefold = false
    var isFlipped = false
    
    var wKingPos = RowCol(row: 7, col: 4)
    var bKingPos = RowCol(row: 0, col: 4)
    
    var last6WhiteMoves = [RowCol]()
    var last6BlackMoves = [RowCol]()
    
    lazy var whiteAI:AIController = AIController(level: 0, isWhite: true, board: self)
    lazy var blackAI:AIController = AIController(level: 0, isWhite: false, board: self)
    
    var lastSelected:RowCol? = nil
    
    override init () {
        super.init()
        setBoard()
    }
    
    required init?(coder aDecoder: NSCoder) {
        board = aDecoder.decodeObject(forKey: "board") as! [[Space]]
        whiteCheck = aDecoder.decodeBool(forKey: "whiteCheck")
        blackCheck = aDecoder.decodeBool(forKey: "blackCheck")
        playerTurn = aDecoder.decodeBool(forKey: "playerTurn")
        whiteScore = aDecoder.decodeInteger(forKey: "whiteScore")
        blackScore = aDecoder.decodeInteger(forKey: "blackScore")
        whiteWins = aDecoder.decodeBool(forKey: "whiteWins")
        blackWins = aDecoder.decodeBool(forKey: "blackWins")
        stalemate = aDecoder.decodeBool(forKey: "stalemate")
        draw = aDecoder.decodeBool(forKey: "draw")
        isFlipped = aDecoder.decodeBool(forKey: "isFlipped")
        threefold = aDecoder.decodeBool(forKey: "threefold")
        wKingPos = aDecoder.decodeObject(forKey: "wKingPos") as! RowCol
        bKingPos = aDecoder.decodeObject(forKey: "bKingPos") as! RowCol
        last6WhiteMoves = aDecoder.decodeObject(forKey: "last6WhiteMoves") as! [RowCol]
        last6BlackMoves = aDecoder.decodeObject(forKey: "last6BlackMoves") as! [RowCol]
        lastSelected = aDecoder.decodeObject(forKey: "lastSelected") as? RowCol ?? nil
    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(board, forKey: "board")
        aCoder.encode(whiteCheck, forKey: "whiteCheck")
        aCoder.encode(blackCheck, forKey: "blackCheck")
        aCoder.encode(playerTurn, forKey: "playerTurn")
        aCoder.encode(whiteScore, forKey: "whiteScore")
        aCoder.encode(blackScore, forKey: "blackScore")
        aCoder.encode(whiteWins, forKey: "whiteWins")
        aCoder.encode(blackWins, forKey: "blackWins")
        aCoder.encode(stalemate, forKey: "stalemate")
        aCoder.encode(draw, forKey: "draw")
        aCoder.encode(threefold, forKey: "threefold")
        aCoder.encode(isFlipped, forKey: "isFlipped")
        aCoder.encode(wKingPos, forKey: "wKingPos")
        aCoder.encode(bKingPos, forKey: "bKingPos")
        aCoder.encode(last6WhiteMoves, forKey: "last6WhiteMoves")
        aCoder.encode(last6BlackMoves, forKey: "last6BlackMoves")
        aCoder.encode(lastSelected, forKey: "lastSelected")
    }
    
    func setBoard () {
        //black's side
        board[0][0] = Rook(isWhite: false)
        board[0][1] = Knight(isWhite: false)
        board[0][2] = Bishop(isWhite: false)
        board[0][3] = Queen(isWhite: false)
        board[0][4] = King(isWhite: false)
        board[0][5] = Bishop(isWhite: false)
        board[0][6] = Knight(isWhite: false)
        board[0][7] = Rook(isWhite: false)
        
        for c in 0...7 {
            board[1][c] = Pawn(isWhite: false)
            board[6][c] = Pawn(isWhite: true)
            
            for r in 2...5 {
                board[r][c] = EmptySpace()
            }
        }
        
        board[7][0] = Rook(isWhite: true)
        board[7][1] = Knight(isWhite: true)
        board[7][2] = Bishop(isWhite: true)
        board[7][3] = Queen(isWhite: true)
        board[7][4] = King(isWhite: true)
        board[7][5] = Bishop(isWhite: true)
        board[7][6] = Knight(isWhite: true)
        board[7][7] = Rook(isWhite: true)
        
    }
    
    func getSpace (_ pos:RowCol) -> Space {
        
        //if pos.row >= 0 && pos.row <= 7 && pos.col >= 0 && pos.col <= 7 {
        return board[pos.row][pos.col]
        //}
    }
    
    func setSpace (_ pos:RowCol, space:Space) {
        board[pos.row][pos.col] = space
    }
    
    func setSpaceWithChecks (_ pos:RowCol, space:Space) {
        board[pos.row][pos.col] = space
        if isSquareAttacked(wKingPos, isWhite: true) {
            whiteCheck = true
        } else {
            whiteCheck = false
        }
        if isSquareAttacked(bKingPos, isWhite: false) {
            blackCheck = true
        } else {
            blackCheck = false
        }
        
        if whiteCheck && checkmate(WHITE) {
            blackWins = true
        } else if blackCheck && checkmate(BLACK) {
            whiteWins = true
        } else if getStalemate() {
            stalemate = true
        }
        
    }
    
    func switchPlayers() {
        playerTurn = !playerTurn
        
        for r in 0...7 {
            for c in 0...7 {
                if let pawn = board[r][c] as? Pawn {
                    if pawn.isWhite() == playerTurn { //en passant stuff
                        pawn.setEnPassantAble(false)
                    }
                }
            }
        }
    }
    
    func selectPiece (_ space:RowCol) {
        
        let row = space.row
        let col = space.col
        
        //print("\(row) x \(col) selected")
        
        if let lastSpace = lastSelected {
            (getSpace(lastSpace) as! Piece).setSelected(false)
        }
        lastSelected = nil
        
        for r in 0...7{
            for c in 0...7{
                board[r][c].setHighlighted(false)
            }
        }
        
        if (row>=0 && row<=7 && col>=0 && col<=7) {
            
            let possibles = possibleMoves(space)
            
            if possibles.count > 0 {
                if let piece = board[row][col] as? Piece {
                    if piece.isWhite() == playerTurn {
                        
                        piece.setSelected(true)
                        lastSelected = space
                        
                        for pos in possibles {
                            board[pos.row][pos.col].setHighlighted(true)
                        }
                    }
                }
            }
        }
    }
    
    func movePiece (_ endPos:RowCol) {
        
        let startRow = lastSelected!.row
        let startCol = lastSelected!.col
        let endRow = endPos.row
        let endCol = endPos.col
        var startPiece = board[startRow][startCol] as! Piece
        let endSpace = board[endRow][endCol]
        
        //threefold repetition
        
        if playerTurn == WHITE {
            last6WhiteMoves.insert(endPos, at: 0)
            if last6WhiteMoves.count == 7 {
                last6WhiteMoves.removeLast()
            }
        } else {
            last6BlackMoves.insert(endPos, at: 0)
            if last6BlackMoves.count == 7 {
                last6BlackMoves.removeLast()
            }
        }
        
        //moving pieces
        
        if startPiece.isSelected() && endSpace.isHighlighted() {
            
            if startPiece.isWhite() {
                whiteScore += endSpace.getValue()
            } else {
                blackScore += endSpace.getValue()
            }
            
            if startPiece is Pawn {
                if endSpace is EmptySpace {
                    if startPiece.isWhite() {
                        if let pawn = board[endRow+1][endCol] as? Pawn {
                            if pawn.isEnPassantAble() {
                                board[endRow+1][endCol] = EmptySpace()
                                whiteScore += 1
                            }
                        }
                    } else {
                        if let pawn = board[endRow-1][endCol] as? Pawn {
                            if pawn.isEnPassantAble() {
                                board[endRow-1][endCol] = EmptySpace()
                                blackScore += 1
                            }
                        }
                    }
                }
                
                if startPiece.isWhite() {
                    if endRow == 0 && whiteAI.level != 0 {
                        startPiece = Queen(isWhite: true)
                    }
                } else {
                    if endRow == 7 && blackAI.level != 0 {
                        startPiece = Queen(isWhite: false)
                    }
                }
            }
            
            if let king = startPiece as? King {
                if king.isWhite() {
                    wKingPos = endPos
                } else {
                    bKingPos = endPos
                }
                
                if king.getHasMoved() == false {
                    king.move()
                    
                    if endCol == 6 {
                        board[startRow][5] = board[startRow][7]
                        board[startRow][7] = EmptySpace()
                        (board[startRow][5] as! Rook).move()
                    }
                    else if endCol == 2 {
                        board[startRow][3] = board[startRow][0]
                        board[startRow][0] = EmptySpace()
                        (board[startRow][3] as! Rook).move()
                    }
                }
            }
            
            if let rook = startPiece as? Rook {
                if rook.getHasMoved() == false {
                    rook.move()
                }
            }
            
            board[endRow][endCol] = startPiece
            board[startRow][startCol] = EmptySpace()
            (board[endRow][endCol] as! Piece).setSelected(false)
            
            if let pawn = board[endRow][endCol] as? Pawn {
                if abs(startRow-endRow) == 2 {
                    pawn.setEnPassantAble(true)
                }
            }
            switchPlayers()
        }
        
        if let piece = board[startRow][startCol] as? Piece {
            piece.setSelected(false)
        }
        
        lastSelected = nil
        
        for r in 0...7 {
            for c in 0...7 {
                board[r][c].setHighlighted(false)
            }
        }
        
        if isSquareAttacked(wKingPos, isWhite: true) {
            whiteCheck = true
        } else {
            whiteCheck = false
        }
        if isSquareAttacked(bKingPos, isWhite: false) {
            blackCheck = true
        } else {
            blackCheck = false
        }
        
        if whiteCheck && checkmate(WHITE) {
            blackWins = true
        } else if blackCheck && checkmate(BLACK) {
            whiteWins = true
        } else if getStalemate() {
            stalemate = true
        }
        
    }
    
    func possibleMoves(_ space:RowCol) -> [RowCol] {
        var piece:Piece
        var result = [RowCol]()
        
        if (getSpace(space) is Piece){
            piece = getSpace(space) as! Piece
        } else {
            return result
        }
        
        switch piece.getType() {
            
        case .empty: return result
        case .pawn: result = pawnMoves(space)
        case .bishop: result = bishopMoves(space)
        case .knight: result = knightMoves(space)
        case .rook: result = rookMoves(space)
        case .queen: result = queenMoves(space)
        case .king: result = kingMoves(space)
            
        }
        
        result = checkMoves(result, startPos: space)
        
        return result
    }
    
    func pawnMoves(_ pawnPos:RowCol) -> [RowCol] {
        let row = pawnPos.row
        let col = pawnPos.col
        
        let pawn = board[row][col] as! Pawn
        
        var moves = [RowCol]();
        
        if pawn.isWhite(){
            
            if (row - 1 >= 0 && board[row-1][col] is EmptySpace){
                moves.append(RowCol(row: row-1, col: col));
            }
            
            if row == 6 {
                if (board[row-2][col] is EmptySpace && board[row-1][col] is EmptySpace){
                    moves.append(RowCol(row: row-2, col: col))
                }
            }
            if (col-1>=0 && row-1>=0) {
                if let piece = board[row-1][col-1] as? Piece {
                    if !piece.isWhite(){
                        moves.append(RowCol(row: row-1, col: col-1));
                    }
                }
            }
            if(col+1<=7 && row-1>=0) {
                if let piece = board[row-1][col+1] as? Piece {
                    if !piece.isWhite(){
                        moves.append(RowCol(row: row-1, col: col+1));
                    }
                }
            }
            
            //checks for en passant
            
            if (col-1>=0){
                if let otherPawn = board[row][col-1] as? Pawn {
                    if otherPawn.isEnPassantAble() {
                        moves.append(RowCol(row: row-1, col: col-1))
                    }
                }
            }
            if (col+1<=7){
                if let otherPawn = board[row][col+1] as? Pawn {
                    if otherPawn.isEnPassantAble() {
                        moves.append(RowCol(row: row-1, col: col+1))
                    }
                }
            }
        } else {
            if (row + 1 <= 7 && board[row+1][col] is EmptySpace){
                moves.append(RowCol(row: row+1, col: col));
            }
            
            if row == 1 {
                if (board[row+2][col] is EmptySpace && board[row+1][col] is EmptySpace){
                    moves.append(RowCol(row: row+2, col: col))
                }
            }
            if (col-1>=0 && row+1<=7) {
                if let piece = board[row+1][col-1] as? Piece {
                    if piece.isWhite(){
                        moves.append(RowCol(row: row+1, col: col-1));
                    }
                }
            }
            if(col+1<=7 && row+1<=7) {
                if let piece = board[row+1][col+1] as? Piece {
                    if piece.isWhite(){
                        moves.append(RowCol(row: row+1, col: col+1));
                    }
                }
            }
            
            //checks for en passant
            
            if (col-1>=0){
                if let otherPawn = board[row][col-1] as? Pawn {
                    if otherPawn.isEnPassantAble() {
                        moves.append(RowCol(row: row+1, col: col-1))
                    }
                }
            }
            if (col+1<=7){
                if let otherPawn = board[row][col+1] as? Pawn {
                    if otherPawn.isEnPassantAble() {
                        moves.append(RowCol(row: row+1, col: col+1))
                    }
                }
            }
        }
        
        return moves
    }
    
    func bishopMoves(_ bishopPos:RowCol) -> [RowCol] {
        let row = bishopPos.row
        let col = bishopPos.col
        
        var moves = [RowCol]()
        
        var i = 1
        
        while isTakeable(bishopPos, pos: RowCol(row: row-i, col: col-i)){ //checks up-left
            moves.append(RowCol(row: row-i, col: col-i))
            if board[row-i][col-i] is Piece{
                i = 10
            }
            i+=1
        }
        
        i = 1
        
        while isTakeable(bishopPos, pos: RowCol(row: row+i, col: col-i)){ //checks down-left
            moves.append(RowCol(row: row+i, col: col-i))
            if board[row+i][col-i] is Piece{
                i = 10
            }
            i+=1
        }
        
        i = 1
        
        while isTakeable(bishopPos, pos: RowCol(row: row-i, col: col+i)){ //checks up-right
            moves.append(RowCol(row: row-i, col: col+i))
            if board[row-i][col+i] is Piece{
                i = 10
            }
            i+=1
        }
        
        i = 1
        
        while isTakeable(bishopPos, pos: RowCol(row: row+i, col: col+i)){ //checks down-right
            moves.append(RowCol(row: row+i, col: col+i))
            if board[row+i][col+i] is Piece{
                i = 10
            }
            i+=1
        }
        
        return moves
    }
    
    func knightMoves(_ knightPos:RowCol) -> [RowCol] {
        let row = knightPos.row
        let col = knightPos.col
        
        var moves = [RowCol]()
        
        
        if isTakeable(knightPos, pos: RowCol(row: row-2, col: col-1)){
            moves.append(RowCol(row: row-2, col: col-1))
        }
        if isTakeable(knightPos, pos: RowCol(row: row-1, col: col-2)){
            moves.append(RowCol(row: row-1, col: col-2))
        }
        if isTakeable(knightPos, pos: RowCol(row: row-1, col: col+2)){
            moves.append(RowCol(row: row-1, col: col+2))
        }
        if isTakeable(knightPos, pos: RowCol(row: row+1, col: col-2)){
            moves.append(RowCol(row: row+1, col: col-2))
        }
        if isTakeable(knightPos, pos: RowCol(row: row+1, col: col+2)){
            moves.append(RowCol(row: row+1, col: col+2))
        }
        if isTakeable(knightPos, pos: RowCol(row: row+2, col: col-1)){
            moves.append(RowCol(row: row+2, col: col-1))
        }
        if isTakeable(knightPos, pos: RowCol(row: row+2, col: col+1)){
            moves.append(RowCol(row: row+2, col: col+1))
        }
        if isTakeable(knightPos, pos: RowCol(row: row-2, col: col+1)){
            moves.append(RowCol(row: row-2, col: col+1))
        }
        
        return moves
    }
    
    func rookMoves(_ rookPos:RowCol) -> [RowCol] {
        let row = rookPos.row
        let col = rookPos.col
        
        var moves = [RowCol]()
        
        var r = row-1
        
        while (isTakeable(rookPos, pos: RowCol(row: r, col: col))){ //checks up
            moves.append(RowCol(row: r, col: col))
            if board[r][col] is Piece {
                r = 10
            }
            r -= 1
        }
        
        r = row+1
        
        while (isTakeable(rookPos, pos: RowCol(row: r, col: col))){ //checks down
            moves.append(RowCol(row: r, col: col))
            if board[r][col] is Piece {
                r = 10
            }
            r += 1
        }
        
        var c = col-1
        
        while (isTakeable(rookPos, pos: RowCol(row: row, col: c))){ //checks left
            moves.append(RowCol(row: row, col: c))
            if board[row][c] is Piece {
                c = 10
            }
            c -= 1
        }
        
        c = col + 1
        
        while (isTakeable(rookPos, pos: RowCol(row: row, col: c))){ //checks right
            moves.append(RowCol(row: row, col: c))
            if board[row][c] is Piece {
                c = 10
            }
            c += 1
        }
        
        return moves
    }
    
    func queenMoves(_ queenPos:RowCol) -> [RowCol] {
        let bMoves = bishopMoves(queenPos)
        var moves = rookMoves(queenPos)
        
        for item in bMoves{
            moves.append(item)
        }
        
        return moves
    }
    
    func kingMoves(_ kingPos:RowCol) -> [RowCol] {
        let row = kingPos.row
        let col = kingPos.col
        let isWhite = (getSpace(kingPos) as! King).isWhite()
        
        var moves = [RowCol]()
        
        if (isTakeable(kingPos, pos: RowCol(row: row-1, col: col-1)) && !isSquareAttacked(RowCol(row: row-1, col: col-1), isWhite: isWhite)){
            moves.append(RowCol(row: row-1, col: col-1))
        }
        if (isTakeable(kingPos, pos: RowCol(row: row-1, col: col)) && !isSquareAttacked(RowCol(row: row-1, col: col), isWhite: isWhite)){
            moves.append(RowCol(row: row-1, col: col))
        }
        if (isTakeable(kingPos, pos: RowCol(row: row-1, col: col+1)) && !isSquareAttacked(RowCol(row: row-1, col: col+1), isWhite: isWhite)){
            moves.append(RowCol(row: row-1, col: col+1))
        }
        if (isTakeable(kingPos, pos: RowCol(row: row, col: col+1)) && !isSquareAttacked(RowCol(row: row, col: col+1), isWhite: isWhite)){
            moves.append(RowCol(row: row, col: col+1))
        }
        if (isTakeable(kingPos, pos: RowCol(row: row+1, col: col+1)) && !isSquareAttacked(RowCol(row: row+1, col: col+1), isWhite: isWhite)){
            moves.append(RowCol(row: row+1, col: col+1))
        }
        if (isTakeable(kingPos, pos: RowCol(row: row+1, col: col)) && !isSquareAttacked(RowCol(row: row+1, col: col), isWhite: isWhite)){
            moves.append(RowCol(row: row+1, col: col))
        }
        if (isTakeable(kingPos, pos: RowCol(row: row+1, col: col-1)) && !isSquareAttacked(RowCol(row: row+1, col: col-1), isWhite: isWhite)){
            moves.append(RowCol(row: row+1, col: col-1))
        }
        if (isTakeable(kingPos, pos: RowCol(row: row, col: col-1)) && !isSquareAttacked(RowCol(row: row, col: col-1), isWhite: isWhite)){
            moves.append(RowCol(row: row, col: col-1))
        }
        
        //Castling
        if (getSpace(kingPos) as! King).getHasMoved() == false && !isSquareAttacked(kingPos, isWhite: isWhite){
            
            if (board[row][col+1] is EmptySpace && board[row][col+2] is EmptySpace){
                if (!isSquareAttacked(RowCol(row: row, col: col+1), isWhite: isWhite) && !isSquareAttacked(RowCol(row: row, col: col+2), isWhite: isWhite)){
                    if (board[row][col+3] is Rook && (board[row][col+3] as! Rook).getHasMoved() == false){
                        moves.append(RowCol(row: row, col: col+2))
                    }
                }
            }
            if (board[row][col-1] is EmptySpace && board[row][col-2] is EmptySpace && board[row][col-3] is EmptySpace){
                if (!isSquareAttacked(RowCol(row: row, col: col-1), isWhite: isWhite) && !isSquareAttacked(RowCol(row: row, col: col-2), isWhite: isWhite) && !isSquareAttacked(RowCol(row: row, col: col-3), isWhite: isWhite)){
                    if (board[row][col-4] is Rook && (board[row][col-4] as! Rook).getHasMoved() == false){
                        moves.append(RowCol(row: row, col: col-2))
                    }
                }
            }
        }
        
        return moves
    }
    
    func isTakeable(_ origin:RowCol, pos:RowCol) -> Bool {
        
        if(pos.col<=7 && pos.col>=0 && pos.row<=7 && pos.row>=0) {
            
            let taker = getSpace(origin) as! Piece
            
            if let space = getSpace(pos) as? Piece {
                if (space.isWhite() == taker.isWhite()){
                    return false
                }
            }
            return true
        }
        return false
    }
    
    func isSquareAttacked(_ pos:RowCol, isWhite:Bool) -> Bool{
        let row = pos.row
        let col = pos.col
        
        //Attacked by:
        //Pawn
        
        if isWhite {
            if (row-1 >= 0 && col-1 >= 0 && board[row-1][col-1] is Pawn && (board[row-1][col-1] as! Pawn).isWhite() == false){
                return true
            }
            if (row-1 >= 0 && col+1 <= 7 && board[row-1][col+1] is Pawn && (board[row-1][col+1] as! Pawn).isWhite() == false){
                return true
            }
        } else {
            if (row+1 <= 7 && col-1 >= 0 && board[row+1][col-1] is Pawn && (board[row+1][col-1] as! Pawn).isWhite() == true){
                return true
            }
            if (row+1 <= 7 && col+1 <= 7 && board[row+1][col+1] is Pawn && (board[row+1][col+1] as! Pawn).isWhite() == true){
                return true
            }
        }
        //Bishop/Queen
        
        var i = 1
        
        while (row-i >= 0 && col-i >= 0) {
            if let piece = board[row-i][col-i] as? Piece {
                if piece.isWhite() != isWhite {
                    if (piece is Bishop || piece is Queen) {
                        return true
                    }
                }
                i = 10
            }
            i += 1
        }
        
        i = 1
        
        while (row+i <= 7 && col-i >= 0) {
            if let piece = board[row+i][col-i] as? Piece {
                if piece.isWhite() != isWhite {
                    if (piece is Bishop || piece is Queen) {
                        return true
                    }
                }
                i = 10
            }
            i += 1
        }
        
        i = 1
        
        while (row-i >= 0 && col+i <= 7) {
            if let piece = board[row-i][col+i] as? Piece {
                if piece.isWhite() != isWhite {
                    if (piece is Bishop || piece is Queen) {
                        return true
                    }
                }
                i = 10
            }
            i += 1
        }
        
        i = 1
        
        while (row+i <= 7 && col+i <= 7) {
            if let piece = board[row+i][col+i] as? Piece {
                if piece.isWhite() != isWhite {
                    if (piece is Bishop || piece is Queen) {
                        return true
                    }
                }
                i = 10
            }
            i += 1
        }
        
        //Rook/Queen
        
        var r = row-1
        
        while (r>=0){ //checks up
            
            if let piece = board[r][col] as? Piece {
                if (piece.isWhite() != isWhite && (piece is Rook || piece is Queen)){
                    return true
                }
                r = -10
            }
            
            r -= 1
        }
        
        r = row+1
        
        while (r<=7){ //checks down
            
            if let piece = board[r][col] as? Piece {
                if (piece.isWhite() != isWhite && (piece is Rook || piece is Queen)){
                    return true
                }
                r = 10
            }
            
            r += 1
        }
        
        var c = col-1
        
        while (c>=0){ //checks left
            
            if let piece = board[row][c] as? Piece {
                if (piece.isWhite() != isWhite && (piece is Rook || piece is Queen)){
                    return true
                }
                c = -10
            }
            c -= 1
        }
        
        c = col + 1
        
        while (c <= 7){ //checks right
            
            if let piece = board[row][c] as? Piece {
                if (piece.isWhite() != isWhite && (piece is Rook || piece is Queen)){
                    return true
                }
                c = 10
            }
            c += 1
        }
        
        //Knight
        
        if row-2 >= 0 && col-1 >= 0{
            if let knight = board[row-2][col-1] as? Knight {
                if (knight.isWhite() != isWhite){
                    return true
                }
            }
        }
        if row-2 >= 0 && col+1 <= 7{
            if let knight = board[row-2][col+1] as? Knight {
                if (knight.isWhite() != isWhite){
                    return true
                }
            }
        }
        if row-1 >= 0 && col-2 >= 0{
            if let knight = board[row-1][col-2] as? Knight {
                if (knight.isWhite() != isWhite){
                    return true
                }
            }
        }
        if row-1 >= 0 && col+2 <= 7{
            if let knight = board[row-1][col+2] as? Knight {
                if (knight.isWhite() != isWhite){
                    return true
                }
            }
        }
        if row+1 <= 7 && col-2 >= 0 {
            if let knight = board[row+1][col-2] as? Knight {
                if (knight.isWhite() != isWhite){
                    return true
                }
            }
        }
        if row+1 <= 7 && col+2 <= 7 {
            if let knight = board[row+1][col+2] as? Knight {
                if (knight.isWhite() != isWhite){
                    return true
                }
            }
        }
        if row+2 <= 7 && col-1 >= 0 {
            if let knight = board[row+2][col-1] as? Knight {
                if (knight.isWhite() != isWhite){
                    return true
                }
            }
        }
        if row+2 <= 7 && col+1 <= 7 {
            if let knight = board[row+2][col+1] as? Knight {
                if (knight.isWhite() != isWhite){
                    return true
                }
            }
        }
        
        //King
        
        if (row-1 >= 0 && col-1 >= 0){
            if let king = board[row-1][col-1] as? King {
                if king.isWhite() != isWhite{
                    return true
                }
            }
        }
        if (row-1>=0){
            if let king = board[row-1][col] as? King {
                if king.isWhite() != isWhite{
                    return true
                }
            }
        }
        if (row-1 >= 0 && col+1 <= 7){
            if let king = board[row-1][col+1] as? King {
                if king.isWhite() != isWhite{
                    return true
                }
            }
        }
        if (col+1 <= 7){
            if let king = board[row][col+1] as? King {
                if king.isWhite() != isWhite{
                    return true
                }
            }
        }
        if (row+1 <= 7 && col+1 <= 7){
            if let king = board[row+1][col+1] as? King {
                if king.isWhite() != isWhite{
                    return true
                }
            }
        }
        if (row+1<=7){
            if let king = board[row+1][col] as? King {
                if king.isWhite() != isWhite{
                    return true
                }
            }
        }
        if (row+1 <= 7 && col-1 >= 0){
            if let king = board[row+1][col-1] as? King {
                if king.isWhite() != isWhite{
                    return true
                }
            }
        }
        if (col-1 >= 0){
            if let king = board[row][col-1] as? King {
                if king.isWhite() != isWhite{
                    return true
                }
            }
        }
        
        return false
    }
    
    func checkMoves(_ input:[RowCol], startPos:RowCol) -> [RowCol] {
        //returns a list of moves that will protect from check or not result in check
        
        var output = [RowCol]()
        var tempStart: Space
        var tempMove: Space
        
        if (whiteCheck || blackCheck) && getSpace(startPos) is King {
            tempStart = getSpace(startPos) as! King
            setSpace(startPos, space: EmptySpace())
            
            for pos in input {
                
                if whiteCheck {
                    if !isSquareAttacked(pos, isWhite: true){
                        output.append(pos)
                    }
                } else {
                    if !isSquareAttacked(pos, isWhite: false){
                        output.append(pos)
                    }
                }
            }
            
            setSpace(startPos, space: tempStart)
            
        }
        else {
            
            tempStart = getSpace(startPos)
            setSpace(startPos, space: EmptySpace())
            
            if whiteCheck && playerTurn == WHITE {
                for pos in input {
                    tempMove = getSpace(pos)
                    setSpace(pos, space: Pawn(isWhite: true))
                    if (!isSquareAttacked(wKingPos, isWhite: true)) {
                        output.append(pos)
                    }
                    setSpace(pos, space: tempMove)
                }
            }
            else if blackCheck && playerTurn == BLACK {
                
                for pos in input {
                    tempMove = getSpace(pos)
                    setSpace(pos, space: Pawn(isWhite: false))
                    if (!isSquareAttacked(bKingPos, isWhite: false)) {
                        output.append(pos)
                    }
                    setSpace(pos, space: tempMove)
                }
            }
            else {
                
                if playerTurn == WHITE && isSquareAttacked(wKingPos, isWhite: true) {
                    for pos in input {
                        tempMove = getSpace(pos)
                        setSpace(pos, space: tempStart)
                        if isSquareAttacked(wKingPos, isWhite: true) == false{
                            output.append(pos)
                        }
                        setSpace(pos, space: tempMove)
                    }
                }
                else if playerTurn == BLACK && isSquareAttacked(bKingPos, isWhite: false) {
                    for pos in input {
                        tempMove = getSpace(pos)
                        setSpace(pos, space: tempStart)
                        if isSquareAttacked(bKingPos, isWhite: false) == false{
                            output.append(pos)
                        }
                        setSpace(pos, space: tempMove)
                    }
                }
                else {
                    output = input
                }
            }
            setSpace(startPos, space: tempStart)
        }
        return output
    }
    
    func checkmate (_ isWhite:Bool) -> Bool {
        for r in 0...7 {
            for c in 0...7 {
                if let piece = board[r][c] as? Piece {
                    if piece.isWhite() == isWhite && possibleMoves(RowCol(row: r, col: c)).count > 0 {
                        return false
                    }
                }
            }
        }
        if (isWhite && whiteCheck) || (!isWhite && blackCheck) {
            return true
        }
        else {return false}
    }
    
    func timesAttacked(_ space:RowCol) -> Int {
        
        var counter = 0
        var attackList:[RowCol]
        
        for r in 0...7 {
            for c in 0...7 {
                if let piece = board[r][c] as? Piece {
                    if piece.isWhite() == playerTurn {
                        attackList = possibleMoves(RowCol(row: r, col: c))
                        
                        for attack in attackList {
                            if attack.equals(space) {
                                counter += 1
                            }
                        }
                    }
                }
            }
        }
        
        return counter
    }
    
    func timesDefended(_ space:RowCol, attacker:RowCol) -> Int {
        var counter = 0
        var defendList:[RowCol]
        let tempAttack = getSpace(attacker)
        setSpace(attacker, space: EmptySpace())
        let tempSpace = getSpace(space)
        setSpace(space, space: Pawn(isWhite: playerTurn))
        
        for r in 0...7 {
            for c in 0...7 {
                if board[r][c] is Piece {
                    defendList = possibleMoves(RowCol(row: r, col: c))
                    
                    for defense in defendList {
                        if defense.equals(space){
                            counter += 1
                        }
                    }
                }
            }
        }
        if getSpace(space) is King {
            counter += 1
        }
        setSpace(space, space: tempSpace)
        setSpace(attacker, space: tempAttack)
        return counter
    }
    
    func shouldTake(_ piece:RowCol, attack:RowCol) -> Bool {
        let attackCounter = timesAttacked(attack)
        let defendCounter = timesDefended(attack, attacker: piece)
        
        if attackCounter > defendCounter {
            return true
        }
        return false
    }
    
    func gameOver() -> Bool {
        if whiteWins || blackWins || getStalemate() {
            return true
        }
        return false
    }
    
    func getStalemate() -> Bool {
        if stalemate {
            return true
        }
        else if !whiteCheck && !blackCheck {
            var result = true
            var counter = 0
            
            for r in 0...7 {
                for c in 0...7 {
                    if let piece = board[r][c] as? Piece {
                        if piece.isWhite() == playerTurn && possibleMoves(RowCol(row: r, col: c)).count > 0 {
                            result = false
                        }
                        counter += 1
                    }
                }
            }
            
            if last6WhiteMoves.count == 6 && last6BlackMoves.count == 6 {
                if last6WhiteMoves[0].equals(last6WhiteMoves[2]) && last6WhiteMoves[2].equals(last6WhiteMoves[4]) {
                    if last6WhiteMoves[1].equals(last6WhiteMoves[3]) && last6WhiteMoves[3].equals(last6WhiteMoves[5]) {
                        if last6BlackMoves[0].equals(last6BlackMoves[2]) && last6BlackMoves[2].equals(last6BlackMoves[4]) {
                            if last6BlackMoves[1].equals(last6BlackMoves[3]) && last6BlackMoves[3].equals(last6BlackMoves[5]) {
                                threefold = true
                                return true
                            }
                        }
                    }
                }
            }
            
            if counter <= 2 {
                draw = true
                return true
            } else {
                return result
            }
        }
        return false
    }
    
}







