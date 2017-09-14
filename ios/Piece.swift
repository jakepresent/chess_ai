//
//  Piece.swift
//  Chess
//
//  Created by Jake Present on 6/15/16.
//  Copyright © 2016 Jake Present. All rights reserved.
//

import Foundation
import UIKit

class Piece : Space {
    fileprivate var selected = false
    fileprivate var color:Bool
    fileprivate var img:UIImage!
    
    init(isWhite:Bool, value:Int, type:TypeOfSpace, image:UIImage) {
        
        color = isWhite
        img = image
        
        super.init(value: value, type: type)
    }
    
    required init?(coder aDecoder: NSCoder) {
        
        selected = aDecoder.decodeBool(forKey: "selected")
        color = aDecoder.decodeBool(forKey: "color")
        
        super.init(coder: aDecoder)
        
        let colorString:String
        let typeString:String
        
        if color {
            colorString = "White"
        } else {
            colorString = "Black"
        }
        
        switch self.getType() {
        case .pawn:
            typeString = "Pawn"
        case .bishop:
            typeString = "Bishop"
        case .knight:
            typeString = "Knight"
        case .rook:
            typeString = "Rook"
        case .queen:
            typeString = "Queen"
        case .king:
            typeString = "King"
        case .empty:
            typeString = "Error"
        }
        
        img = UIImage(named: "\(colorString)\(typeString)")
    }
    
    override func encode(with aCoder: NSCoder) {
        super.encode(with: aCoder)
        aCoder.encode(selected, forKey: "selected")
        aCoder.encode(color, forKey: "color")
    }
    
    func isSelected() -> Bool{
        return selected
    }
    
    func setSelected(_ selected:Bool){
        self.selected = selected
    }
    
    func isWhite() -> Bool {
        return color
    }
    
    func getImage() -> UIImage {
        return img
    }
    
    func setImage(_ image:UIImage) {
        img = image
    }
}
