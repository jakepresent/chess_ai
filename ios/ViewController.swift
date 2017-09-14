//
//  ViewController.swift
//  Chess
//
//  Created by Jake Present on 6/13/16.
//  Copyright © 2016 Jake Present. All rights reserved.
//

import UIKit
import AVFoundation

class ViewController: UIViewController {
    
    @IBOutlet weak var boardView: UIImageView!
    @IBOutlet weak var turnLabel: UILabel!
    @IBOutlet weak var whiteScoreLabel: UILabel!
    @IBOutlet weak var blackScoreLabel: UILabel!
    @IBOutlet weak var whiteAIButton: UIButton!
    @IBOutlet weak var blackAIButton: UIButton!
    @IBOutlet weak var chessLabel: UILabel!
    @IBOutlet weak var byJakePresentLabel: UILabel!
    @IBOutlet weak var restartButton: UIBarButtonItem!
    @IBOutlet weak var flipButton: UIBarButtonItem!
    
    //Constraints
    @IBOutlet weak var boardCenterX: NSLayoutConstraint!
    @IBOutlet weak var boardBottomAlign: NSLayoutConstraint!
    @IBOutlet weak var labelsAlignLeft: NSLayoutConstraint!
    @IBOutlet weak var labelsAlignBoard: NSLayoutConstraint!
    @IBOutlet weak var bottomTurnLabel50: NSLayoutConstraint!
    @IBOutlet weak var bottomTurnLabel10: NSLayoutConstraint!
    @IBOutlet weak var boardAlignTop1: NSLayoutConstraint!
    @IBOutlet weak var boardAlignTop2: NSLayoutConstraint!
    @IBOutlet weak var boardAlignTop3: NSLayoutConstraint!
    
    
    var squareSize: CGFloat = UIScreen.main.bounds.width/8.0
    
    var board = Board()
    var isLooping = false
    var clickSoundEffect = AVAudioPlayer()
    var timer = Timer()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if UIDevice.current.orientation.isPortrait {
            applyPortraitConstraint()
        } else {
            applyLandscapeConstraint()
        }
        
        if UIScreen.main.bounds.height == 480 {
            chessLabel.isHidden = true
            byJakePresentLabel.isHidden = true
        }
        
        let defaults = UserDefaults.standard
        
        if let savedBoard = defaults.object(forKey: "savedBoard") as? Data {
            board = NSKeyedUnarchiver.unarchiveObject(with: savedBoard) as! Board
        }
        
        let font = UIFont(name: "Gill Sans", size: 19)!
        restartButton.setTitleTextAttributes([NSFontAttributeName: font], for: UIControlState())
        flipButton.setTitleTextAttributes([NSFontAttributeName: font], for: UIControlState())
        
        drawView()
        
    }
    
    
    func drawView() {
        
        drawBoard()
        setLabels()
        save()
    }
    
    func drawBoard() {
        
        UIGraphicsBeginImageContextWithOptions(CGSize(width: 320, height: 320), false, 0)
        let context = UIGraphicsGetCurrentContext()
        
        let boardImage = UIImage(named: "Board")
        boardImage?.draw(at: CGPoint(x: 0, y: 0))
        
        let blueColor = UIColor.init(red: 0.0, green: 0.7842, blue: 1.0, alpha: 0.47)
        
        for r in 0...7 {
            for c in 0...7 {
                
                let space = board.getSpace(RowCol(row: r, col: c))
                
                if space.isHighlighted() {
                    
                    let rectangle:CGRect
                    
                    if board.isFlipped {
                        rectangle = CGRect(x: (7-c)*40, y: (7-r)*40, width: 40, height: 40)
                    } else {
                        rectangle = CGRect(x: c*40, y: r*40, width: 40, height: 40)
                    }
                    
                    context?.setFillColor(blueColor.cgColor)
                    context?.addRect(rectangle)
                    context?.drawPath(using: .fill)
                }
                if let piece = space as? Piece {
                    
                    let rectangle:CGRect
                    
                    if piece.isSelected() {
                        
                        if board.isFlipped {
                            rectangle = CGRect(x: (7-c)*40, y: (7-r)*40, width: 40, height: 40)
                        } else {
                            rectangle = CGRect(x: c*40, y: r*40, width: 40, height: 40)
                        }
                        context?.setFillColor(blueColor.cgColor)
                        context?.addRect(rectangle)
                        context?.drawPath(using: .fill)
                    }
                    
                    let image = piece.getImage()
                    
                    if board.isFlipped {
                        image.draw(at: CGPoint(x: (7-c)*40, y: (7-r)*40))
                    } else {
                        image.draw(at: CGPoint(x: c*40, y: r*40))
                    }
                }
            }
        }
        
        
        let img = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        boardView.image = img
    }
    
    func setLabels() {
        whiteScoreLabel.text! = "White's Score: \(board.whiteScore)"
        blackScoreLabel.text! = "Black's Score: \(board.blackScore)"
        if whiteScoreLabel.adjustsFontSizeToFitWidth {
            whiteScoreLabel.font = UIFont(name: "Gill Sans", size: 1.0)
        }
        
        if board.playerTurn == true {
            turnLabel.text! = "White's Turn"
        } else {
            turnLabel.text! = "Black's Turn"
        }
        
        if board.whiteCheck {
            turnLabel.text! = "White is in check"
        }
        else if board.blackCheck {
            turnLabel.text! = "Black is in check"
        }
        if board.getStalemate() {
            turnLabel.text! = "Stalemate"
            if  board.threefold {
                turnLabel.text! = "Threefold Repetition"
            } else if board.draw {
                turnLabel.text! = "Draw"
            }
        }
        if board.blackWins {
            turnLabel.text! = "Black wins!"
        }
        else if board.whiteWins {
            turnLabel.text! = "White wins!"
        }
    }
    
    @IBAction func shareTapped(_ sender: UIBarButtonItem) {
        let vc = UIActivityViewController(activityItems: [boardView.image!], applicationActivities: [])
        vc.excludedActivityTypes = [UIActivityType.assignToContact]
        vc.popoverPresentationController?.barButtonItem = sender
        present(vc, animated: true, completion: nil)
    }
    
    @IBAction func restartTapped(_ sender: UIBarButtonItem) {
        board = Board()
        
        whiteAIButton.setTitle("White: Human", for: UIControlState())
        blackAIButton.setTitle("Black: Human", for: UIControlState())
        
        drawView()
    }
    
    @IBAction func flipTapped(_ sender: UIBarButtonItem) {
        board.isFlipped = !board.isFlipped
        drawView()
    }
    
    func playSound() {
        let path = Bundle.main.path(forResource: "click", ofType: "wav")!
        let url = URL(fileURLWithPath: path)
        
        do {
            clickSoundEffect = try AVAudioPlayer(contentsOf: url)
            clickSoundEffect.play()
        }
        catch {
            print("file did not load")
        }
    }
    
    func updateTimer() {
        
        if !board.gameOver() {
        
            if board.whiteAI.level != 0 && board.blackAI.level == 0 && board.playerTurn == true {
                board.whiteAI.move()
                playSound()
            }
            else if board.blackAI.level != 0 && board.whiteAI.level == 0 && board.playerTurn == false {
                board.blackAI.move()
                playSound()
            }
        }
        
        if isLooping == false || board.whiteAI.level == 0 || board.blackAI.level == 0 {
            timer.invalidate()
            isLooping = false
        } else {
            if (board.playerTurn == true) {
                board.whiteAI.move()
                playSound()
            } else {
                board.blackAI.move()
                playSound()
            }
            
            if board.gameOver() {
                timer.invalidate()
                isLooping = false
            }
        }
        drawView()
    }
    
    @IBAction func AITapped(_ sender: UIButton) {
        
        let title = sender.currentTitle!
        let color: String
        let isWhite: Bool
        
        if title.contains("White") {
            color = "white"
            isWhite = true
        } else {
            color = "black"
            isWhite = false
        }
        
        let ac = UIAlertController(title: "Type of AI for \(color)", message: nil, preferredStyle: .actionSheet)
        let humanAction = UIAlertAction(title: "Human", style: .default) { [unowned self, sender] (action:UIAlertAction) in
            
            if isWhite {
                self.board.whiteAI.level = 0
            } else {
                self.board.blackAI.level = 0
            }
            
            sender.setTitle("\(color.capitalized): Human", for: UIControlState())
        }
        let ai1Action = UIAlertAction(title: "Random AI", style: .default) { [unowned self, sender] (action:UIAlertAction) in
            
            if isWhite {
                self.board.whiteAI.level = 1
            } else {
                self.board.blackAI.level = 1
            }
            
            sender.setTitle("\(color.capitalized): Random", for: UIControlState())
        }
        
        let ai2Action = UIAlertAction(title: "Stupid AI", style: .default) { [unowned self, sender] (action:UIAlertAction) in
            if isWhite {
                self.board.whiteAI.level = 2
            } else {
                self.board.blackAI.level = 2
            }
            sender.setTitle("\(color.capitalized): Stupid", for: UIControlState())
        }
        
        let ai3Action = UIAlertAction(title: "Smart AI", style: .default) { [unowned self, sender] (action:UIAlertAction) in
            if isWhite {
                self.board.whiteAI.level = 3
            } else {
                self.board.blackAI.level = 3
            }
            sender.setTitle("\(color.capitalized): Smart", for: UIControlState())
        }
        
        ac.addAction(humanAction)
        ac.addAction(ai1Action)
        ac.addAction(ai2Action)
        ac.addAction(ai3Action)
        ac.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        
        ac.popoverPresentationController?.sourceView = sender
        ac.popoverPresentationController?.sourceRect = sender.bounds
        
        present(ac, animated: true, completion: nil)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        
        if !board.gameOver() {
            
            if let touch = touches.first {
                let point:CGPoint = touch.location(in: boardView)
                let pos:RowCol
                
                
                if board.isFlipped {
                    pos = RowCol(row: 7 - Int(point.y/squareSize), col: 7 - Int(point.x/squareSize))
                } else {
                    pos = RowCol(row: Int(point.y/squareSize), col: Int(point.x/squareSize))
                }
                
                if pos.row >= 0 && pos.row <= 7 && pos.col >= 0 && pos.col <= 7 {
                    
                    if !board.gameOver() && board.whiteAI.level == 0 || board.blackAI.level == 0 {
                        
                        if board.getSpace(pos).isHighlighted() == false {
                            board.selectPiece(pos)
                        } else {
                            board.movePiece(pos)
                            
                            if let pawn = board.getSpace(pos) as? Pawn {
                                if pawn.isWhite() && pos.row == 0 {
                                    presentPromotionAlertController(pos, isWhite: pawn.isWhite())
                                } else if !pawn.isWhite() && pos.row == 7 {
                                    presentPromotionAlertController(pos, isWhite: pawn.isWhite())
                                }
                            }
                            
                            playSound()
                        }
                    }
                }
                
                
                //moves AI
                if board.playerTurn == true && board.whiteAI.level != 0 && board.blackAI.level == 0 {
                    timer = Timer.scheduledTimer(timeInterval: 0.3, target: self, selector: #selector(updateTimer), userInfo: nil, repeats: false)
                }
                if board.playerTurn == false && board.blackAI.level != 0 && board.whiteAI.level == 0 {
                    timer = Timer.scheduledTimer(timeInterval: 0.3, target: self, selector: #selector(updateTimer), userInfo: nil, repeats: false)
                }
                
                if board.whiteAI.level != 0 && board.blackAI.level != 0 {
                    if isLooping == false {
                        isLooping = true
                        timer = Timer.scheduledTimer(timeInterval: 0.3, target: self, selector: #selector(updateTimer), userInfo: nil, repeats: true)
                    }
                }
                
                drawView()
            }
            
        }
        
    }
    
    override var prefersStatusBarHidden : Bool {
        return true
    }
    
    func applyPortraitConstraint(){
        
        squareSize = UIScreen.main.bounds.width/8.0
        
        boardCenterX.isActive = true
        labelsAlignLeft.isActive = true
        
        bottomTurnLabel10.isActive = true
        bottomTurnLabel50.isActive = true
        boardAlignTop1.isActive = true
        boardAlignTop2.isActive = true
        boardBottomAlign.isActive = true
        
        boardBottomAlign.isActive = false
        labelsAlignBoard.isActive = false
        
    }
    
    func applyLandscapeConstraint(){
        
        squareSize = 85
        
        boardCenterX.isActive = false
        labelsAlignLeft.isActive = false
        
        bottomTurnLabel10.isActive = false
        bottomTurnLabel50.isActive = false
        boardAlignTop1.isActive = false
        boardAlignTop2.isActive = false
        boardBottomAlign.isActive = false
        
        boardBottomAlign.isActive = true
        labelsAlignBoard.isActive = true
        boardAlignTop3.isActive = true

    }
    
    
    func presentPromotionAlertController(_ pos:RowCol, isWhite:Bool) {
        
        
        let ac = UIAlertController(title: "Promote pawn to:", message: nil, preferredStyle: .alert)
        ac.addAction(UIAlertAction(title: "Queen", style: .default) {[unowned self] _ in
            self.board.setSpaceWithChecks(pos, space: Queen(isWhite: isWhite))
            self.drawView()
            })
        ac.addAction(UIAlertAction(title: "Rook", style: .default) {[unowned self] _ in
            self.board.setSpaceWithChecks(pos, space: Rook(isWhite: isWhite))
            self.drawView()
            })
        ac.addAction(UIAlertAction(title: "Bishop", style: .default) {[unowned self] _ in
            self.board.setSpaceWithChecks(pos, space: Bishop(isWhite: isWhite))
            self.drawView()
            })
        ac.addAction(UIAlertAction(title: "Knight", style: .default) {[unowned self] _ in
            self.board.setSpaceWithChecks(pos, space: Knight(isWhite: isWhite))
            self.drawView()
            })
        
        present(ac, animated: true, completion: nil)
        
    }
    
    func save() {
        let savedData = NSKeyedArchiver.archivedData(withRootObject: board)
        let defaults = UserDefaults.standard
        
        defaults.set(savedData, forKey: "savedBoard")
    }
    
    override func viewWillTransition(to size: CGSize, with coordinator:    UIViewControllerTransitionCoordinator) {
        
        
        coordinator.animate(alongsideTransition: { (UIViewControllerTransitionCoordinatorContext) -> Void in
            
            let orient = UIApplication.shared.statusBarOrientation
            
            switch orient {
            case .portrait:
                self.applyPortraitConstraint()
                break
            default:
                self.applyLandscapeConstraint()
                break
            }
            }, completion: nil)
        super.viewWillTransition(to: size, with: coordinator)
        
    }
}

