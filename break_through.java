import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;

//2016.12.3 ZXZ


class Pawn{
	public int i;
	public int j;
	public boolean State;    //true is alive, false is dead
	Pawn(int ii,int jj, boolean state){
		i=ii;j=jj;State=state;
	}
}

class MAP{
	//public static playmodule PM;
	//1 means white cover; 2 means white ready to move; 3 means white ready to come; 4 means white to be killed
	//-1 means black cover; -2 means black ready to move; -3 means black ready to come; -4 means black to be killed
	//0 means null
    //[i][j] i means vertical axis, j means horizontal axis
	int MapStatue[][]=new int [8][8]; 
	public int MapStatueRecord[][][]=new int [1000][8][8];
	public Pawn BlackTeam[]=new Pawn[16];
	public int StepNumber=0;
	public int Round=1; 
	public int WhiteBlackWin = 0; // 0 means null , 1 means white win , -1 means black win
	public boolean WhiteBlackTurn= true;    //true means white ,false means black
	public boolean WhiteBlackFirst= true;    //true means white first,false means black first
	public boolean Hold= false;   //true means pawn is being held 
	public boolean Playmodule=true;    //true means Player vs AI, else Player vs Player
	public int BlackDeadList[]=new int [1000];  //code form: i*10+j
	public int BlackMoveList[]=new int [1000];  //code from: i'*1000+j'*100+i*10+j
	public boolean BlackDead= false;        //true means one pawn has been killed right now 
	public boolean WhiteDead= false;
	public int Order[]={3,4,1,6,0,7,8,15,11,12,2,5,10,13,9,14};
	public int AverageOrder[]={3,4,1,6,0,7,8,15,11,12,2,5,10,13,9,14};
	public int LeftOrder[]={3,4,1,0,8,6,7,15,11,12,2,5,10,13,9,14};
	public int RightOrder[]={3,4,6,7,15,1,0,8,11,12,2,5,10,13,9,14};
	public int Teampopulation=16;
	public MAP(){
		
		for (int j=0;j<8;++j){
			MapStatue[0][j]=MapStatue[1][j]=1;
			MapStatue[6][j]=MapStatue[7][j]=-1;	
		}
	}
	
	public void Restart(){
		for (int j=0;j<8;++j){
			MapStatue[0][j]=MapStatue[1][j]=1;
			MapStatue[6][j]=MapStatue[7][j]=-1;	
		}
		for (int i=2;i<6;++i){
			for (int j=0;j<8;++j){
				MapStatue[i][j]=0;
			}
		}
		for (int ii=6;ii<8;++ii){
			for (int jj=0;jj<8;++jj){
				BlackTeam[(ii-6)*8+jj]=new Pawn(ii,jj,true);
			}
		}
		StepNumber=0;
		Round =1;
		WhiteBlackTurn= true;
		BlackDead= false;
		WhiteDead= false;
		Hold= false;
		MapStatueRecord=new int [1000][8][8];
		BlackDeadList=new int [1000];
		BlackMoveList=new int [1000];
		WhiteBlackWin = 0 ;
		for (int i=0;i<16;++i){
			Order[i]=AverageOrder[i];
		}
		for (int ii=6;ii<8;++ii){
			for (int jj=0;jj<8;++jj){
				BlackTeam[(ii-6)*8+jj]=new Pawn(ii,jj,true);
			}
		}
	}
	
	public void CopyMapStatue(int origin[][],int copy[][]){
		for (int i=0;i<8;++i){
			for (int j=0;j<8;++j){
				copy[i][j]=origin[i][j];
			}
		}
	}
	public void DetectWhoWin (){
		for (int j=0; j<8; ++j ){
			if (MapStatue[0][j]==-1){
				WhiteBlackWin = -1 ;
				return;
			}
				
		    if (MapStatue[7][j]==1){
		    	WhiteBlackWin = 1 ;
		    	return;
		    }
		    	
		}
	}
	public void Manipulation(int i,int j,int step){
		int round= (step-1)/3+1;
		switch (MapStatue[i][j]){
		case 0:
			StepNumber--;
			break;
		case 1:
			if (!WhiteBlackTurn || Hold){
				StepNumber--;
				return;
			}
				
			MapStatue[i][j]=2;
			if (MapStatue[i+1][j]==0) MapStatue[i+1][j]=3;
			if (j>0){
				if (MapStatue[i+1][j-1]==0) MapStatue[i+1][j-1]=3;
				else if (MapStatue[i+1][j-1]==-1) MapStatue[i+1][j-1]=-4;
				
			}
			if (j<7) {
				if (MapStatue[i+1][j+1]==0) MapStatue[i+1][j+1]=3;
				else if (MapStatue[i+1][j+1]==-1) MapStatue[i+1][j+1]=-4;
			}
			Hold=true;	
		    break;
		case 2:
			MapStatue[i][j]=1;
			StepNumber-=2;
			if (MapStatue[i+1][j]==3) MapStatue[i+1][j]=0;
			if (j>0){
				if (MapStatue[i+1][j-1]==3) MapStatue[i+1][j-1]=0;
				else if (MapStatue[i+1][j-1]==-4) MapStatue[i+1][j-1]=-1;			
			}
			if (j<7) {
				if (MapStatue[i+1][j+1]==3) MapStatue[i+1][j+1]=0;
				else if (MapStatue[i+1][j+1]==-4) MapStatue[i+1][j+1]=-1;
			}
			Hold=false;	
		    break;
		case 3:
			MapStatue[i][j]=1;
			if (MapStatue[i-1][j]==2) MapStatue[i-1][j]=0;
			if (j>0){
				if (MapStatue[i-1][j-1]==2) MapStatue[i-1][j-1]=0;
				if (j>1){
					if(MapStatue[i][j-2]==3)	
						MapStatue[i][j-2]=0;
					else if(MapStatue[i][j-2]==-4)	
						MapStatue[i][j-2]=-1;
				}
				if (MapStatue[i][j-1]==3) MapStatue[i][j-1]=0;
				else if (MapStatue[i][j-1]==-4) MapStatue[i][j-1]=-1;
			}
			if (j<7) {
				if (MapStatue[i-1][j+1]==2) MapStatue[i-1][j+1]=0;	
				if (j<6) {
					if (MapStatue[i][j+2]==3)
						MapStatue[i][j+2]=0;
					else if (MapStatue[i][j+2]==-4)
						MapStatue[i][j+2]=-1;
				}
				if (MapStatue[i][j+1]==3) MapStatue[i][j+1]=0;
				else if (MapStatue[i][j+1]==-4) MapStatue[i][j+1]=-1;
			}
			WhiteBlackTurn= false;
			Hold=false;
		    break;
		case 4:
			MapStatue[i][j]=-1;
			if (MapStatue[i+1][j]==-2) MapStatue[i+1][j]=0;
			if (j>0){
				if (MapStatue[i+1][j-1]==-2) MapStatue[i+1][j-1]=0;	
				if (MapStatue[i][j-1]==-3) MapStatue[i][j-1]=0;
				if (j>1) {
					if (MapStatue[i][j-2]==4)
						MapStatue[i][j-2]=1;
					else if (MapStatue[i][j-2]==-3)
						MapStatue[i][j-2]=0;
				}
			}
			if (j<7) {
				if (MapStatue[i+1][j+1]==-2) MapStatue[i+1][j+1]=0;	
				if (MapStatue[i][j+1]==-3) MapStatue[i][j+1]=0;
				if (j<6) {
					if (MapStatue[i][j+2]==4)
						MapStatue[i][j+2]=1;
					else if (MapStatue[i][j+2]==-3)
						MapStatue[i][j+2]=0;
				}
			}
			WhiteBlackTurn= true;
			Hold=false;
			//WhiteDeadList[step]=i*10+j;
			WhiteDead=true;
		    break;
		case -1:
			if (WhiteBlackTurn || Hold){
				StepNumber--;
				return;
			}
			MapStatue[i][j]=-2;
			if (MapStatue[i-1][j]==0) MapStatue[i-1][j]=-3;
			if (j>0){
				if (MapStatue[i-1][j-1]==0) MapStatue[i-1][j-1]=-3;
				else if (MapStatue[i-1][j-1]==1) MapStatue[i-1][j-1]=4;	
			}
			if (j<7) {
				if (MapStatue[i-1][j+1]==0) MapStatue[i-1][j+1]=-3;
				else if (MapStatue[i-1][j+1]==1) MapStatue[i-1][j+1]=4;
			}
			Hold=true;
		    break;
		case -2:
			MapStatue[i][j]=-1;
			StepNumber-=2;
			if (MapStatue[i-1][j]==-3) MapStatue[i-1][j]=0;
			if (j>0){
				if (MapStatue[i-1][j-1]==-3) MapStatue[i-1][j-1]=0;
				else if (MapStatue[i-1][j-1]==4) MapStatue[i-1][j-1]=1;		
			}
			if (j<7) {
				if (MapStatue[i-1][j+1]==-3) MapStatue[i-1][j+1]=0;
				else if (MapStatue[i-1][j+1]==4) MapStatue[i-1][j+1]=1;
			}
			Hold=false;
		    break;
		case -3:
			MapStatue[i][j]=-1;
			if (MapStatue[i+1][j]==-2) MapStatue[i+1][j]=0;
			if (j>0){
				if (MapStatue[i+1][j-1]==-2) MapStatue[i+1][j-1]=0;	
				if (j>1){
					if(MapStatue[i][j-2]==-3)	
						MapStatue[i][j-2]=0;
					else if(MapStatue[i][j-2]==4)	
						MapStatue[i][j-2]=1;
				}
				if (MapStatue[i][j-1]==-3) MapStatue[i][j-1]=0;
				else if (MapStatue[i][j-1]==4) MapStatue[i][j-1]=1;
			}
			if (j<7) {
				if (MapStatue[i+1][j+1]==-2) MapStatue[i+1][j+1]=0;	
				if (j<6) {
					if (MapStatue[i][j+2]==-3)
						MapStatue[i][j+2]=0;
					else if (MapStatue[i][j+2]==4)
						MapStatue[i][j+2]=1;
				}
				if (MapStatue[i][j+1]==-3) MapStatue[i][j+1]=0;
				else if (MapStatue[i][j+1]==4) MapStatue[i][j+1]=1;
			}
			WhiteBlackTurn= true;
			Hold=false;
		    break;   
		case -4:
			MapStatue[i][j]=1;
			if (MapStatue[i-1][j]==2) MapStatue[i-1][j]=0;
			
			if (j>0){
				if (MapStatue[i-1][j-1]==2) MapStatue[i-1][j-1]=0;	
				if (MapStatue[i][j-1]==3) MapStatue[i][j-1]=0;
				if (j>1) {
					if (MapStatue[i][j-2]==-4)
						MapStatue[i][j-2]=-1;
					else if (MapStatue[i][j-2]==3)
						MapStatue[i][j-2]=0;
				}
			}
			if (j<7) {
				if (MapStatue[i-1][j+1]==2) MapStatue[i-1][j+1]=0;
				if (MapStatue[i][j+1]==3) MapStatue[i][j+1]=0;
				if (j<6) {
					if (MapStatue[i][j+2]==-4)
						MapStatue[i][j+2]=-1;
					else if (MapStatue[i][j+2]==3)
						MapStatue[i][j+2]=0;
				}
			}
			WhiteBlackTurn= false;
			Hold=false;
			BlackDeadList[round]=i*10+j;
			BlackDead=true;
		    break;   
		}
	}
}




class AI extends MAP{
	//black only

	
	AI(){
		for (int ii=6;ii<8;++ii){
			for (int jj=0;jj<8;++jj){
				BlackTeam[(ii-6)*8+jj]=new Pawn(ii,jj,true);
			}
		}
	}
	
	public void StraightAhead(Pawn soldier,int round){
		MapStatue[soldier.i][soldier.j]=0;
		MapStatue[soldier.i-1][soldier.j]=-1;	
		--soldier.i;
		BlackMoveList[round]= soldier.i*1000+soldier.j*100+(soldier.i+1)*10+soldier.j;
	}
	
	public void RightDiagonal(Pawn soldier,int round){
		MapStatue[soldier.i][soldier.j]=0;
		MapStatue[soldier.i-1][soldier.j+1]=-1;	
		--soldier.i;
		++soldier.j;
		BlackMoveList[round]= soldier.i*1000+soldier.j*100+(soldier.i+1)*10+(soldier.j-1);
	}
	
	public void LeftDiagonal(Pawn soldier,int round){
		MapStatue[soldier.i][soldier.j]=0;
		MapStatue[soldier.i-1][soldier.j-1]=-1;	
		--soldier.i;
		--soldier.j;
		BlackMoveList[round]= soldier.i*1000+soldier.j*100+(soldier.i+1)*10+(soldier.j+1);
	}
	
	public void AnalysisBlack(int Step){ 
		int firststep,secondstep;
		int LeftDefendPower=0,RightDefendPower=0;    //the more minus value, the stronger black power
		int LeftAttackPower=0,RightAttackPower=0;      //define attackpower to choose right attack direction  
		boolean Straight=false,Left=false,Right=false;
	    int round = (Step-1)/3+1;
		if (Step>5) {
			CopyMapStatue(MapStatueRecord[Step],MapStatue);
			for (int i=0;i<8;++i){                     //decide the trend of battle
				for (int j=0;j<8;++j){
					if (j<4) {
						if (i>2)
							LeftDefendPower+=MapStatue[i][j];
						if (i<4)
							LeftAttackPower+=MapStatue[i][j];
					}
					else {
						if (i>2)
							RightDefendPower+=MapStatue[i][j];
						if (i<4)
							RightAttackPower+=MapStatue[i][j];
					}
					
				}
			}
		}
		if (WhiteBlackFirst){
			firststep=2;
			secondstep=5;
		}
		else {
			firststep=0;
			secondstep=3;
		}
		if (Step==firststep) StraightAhead(BlackTeam[3],round);
		else if (Step==secondstep) StraightAhead(BlackTeam[4],round);
		else {	
			for (int n=0;n<Teampopulation;++n){                 //to reduce population if any soldier died
				if (BlackDead && BlackTeam[Order[n]].i==BlackDeadList[round]/10 && BlackTeam[Order[n]].j==BlackDeadList[round]%10) {
					BlackTeam[Order[n]].State=false;					
					for (int nn=n;nn<Teampopulation-1;++nn){
						int sub=Order[nn];
						Order[nn]=Order[nn+1];
						Order[nn+1]=sub;
					}
					--Teampopulation;
					BlackDead=false;
					break;
				}
			}
           
			for (int n=0;n<Teampopulation;++n){                                             //to decide priority order to move				
				for (int nn=n;nn>0;--nn){        //front one first to move
					if (BlackTeam[Order[nn]].j>=4 && BlackTeam[Order[nn-1]].j>=4) {
						if (BlackTeam[Order[nn]].i<BlackTeam[Order[nn-1]].i){
							int sub=Order[nn];
							Order[nn]=Order[nn-1];
							Order[nn-1]=sub;
						}
						else break;
					}
					else if (BlackTeam[Order[nn]].j<4 && BlackTeam[Order[nn-1]].j<4) {
						if (BlackTeam[Order[nn]].i<BlackTeam[Order[nn-1]].i){
							int sub=Order[nn];
							Order[nn]=Order[nn-1];
							Order[nn-1]=sub;
						}
						else break;
					}
					else if (BlackTeam[Order[nn]].j>=4 && BlackTeam[Order[nn-1]].j<4 ) {     //the power of each side decide the priority
						if ( LeftDefendPower>RightDefendPower && !( BlackTeam[Order[nn-1]].i<4 && BlackTeam[Order[nn]].i>=4 ) ) {
							int sub=Order[nn];
							Order[nn]=Order[nn-1];
							Order[nn-1]=sub;
						}
						else if ( LeftDefendPower<RightDefendPower && ( BlackTeam[Order[nn-1]].i>=4 && BlackTeam[Order[nn]].i<4 )) {
							int sub=Order[nn];
							Order[nn]=Order[nn-1];
							Order[nn-1]=sub;
						}
						else break;
					}
					else if (BlackTeam[Order[nn]].j<4 && BlackTeam[Order[nn-1]].j>=4 ) {
						if ( LeftDefendPower<RightDefendPower && !( BlackTeam[Order[nn-1]].i<4 && BlackTeam[Order[nn]].i>=4 )) {
							int sub=Order[nn];
							Order[nn]=Order[nn-1];
							Order[nn-1]=sub;
						}
						else if ( LeftDefendPower>RightDefendPower && ( BlackTeam[Order[nn-1]].i>=4 && BlackTeam[Order[nn]].i<4 )) {
							int sub=Order[nn];
							Order[nn]=Order[nn-1];
							Order[nn-1]=sub;
						}
						else break;
					}
				}
			}
			
			int count=0;
			for (int n=0;n<Teampopulation;++n){             //keep defend rally                                 
				if (count<Teampopulation) ++count;			
				else break;
				if (BlackTeam[Order[n]].i>4 && ( MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j]==1 || MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]==1 ) ){
					for (int nn=n;nn<Teampopulation-1;++nn){
						int sub=Order[nn+1];
						Order[nn+1]=Order[nn];
						Order[nn]=sub;
					}
					--n;
					continue;
				}
				if (BlackTeam[Order[n]].i==7){
					if (BlackTeam[Order[n]].j>1){
						if (MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j-2]==1) {							
							for (int nn=n;nn<Teampopulation-1;++nn){
								int sub=Order[nn+1];
								Order[nn+1]=Order[nn];
								Order[nn]=sub;
							}
							--n;
							continue;
						}
					}
					if (BlackTeam[Order[n]].j<6){
						if (MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j+2]==1) {
							for (int nn=n;nn<Teampopulation-1;++nn){
								int sub=Order[nn+1];
								Order[nn+1]=Order[nn];
								Order[nn]=sub;
							}
							--n;
							continue;
						}
					}
				}
			}
			
			for (int n=0;n<Teampopulation;++n){                 //match point measure       
				if ( BlackTeam[Order[n]].i==1 ){            //last second line in high priority to attack
					if (BlackTeam[Order[n]].j>0) {LeftDiagonal(BlackTeam[Order[n]],round);++StepNumber;return;}
					if (BlackTeam[Order[n]].j<7) {RightDiagonal(BlackTeam[Order[n]],round);++StepNumber;return;}
				}
				if ( BlackTeam[Order[n]].i==7 ){            //baseline high priority to defend
					if (BlackTeam[Order[n]].j>=1){
						if (MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j-1]==1) {
							LeftDiagonal(BlackTeam[Order[n]],round);++StepNumber;return;
						}
					}
					if (BlackTeam[Order[n]].j<=6){
						if (MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j+1]==1) {
							RightDiagonal(BlackTeam[Order[n]],round);++StepNumber;return;
						}
					}
				}
				
			}
			for (int n=0;n<Teampopulation;++n){  
				if ( BlackTeam[Order[n]].i==2 ){            //to keep attack effective, ignoring small benefit
					if (BlackTeam[Order[n]].j<6) {
						int A_2=MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j+2]*(MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j+2]-1);
						int O_2=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]+1);
						int O_3=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j+2]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j+2]+1);
						if ( ( A_2 - O_2 -O_3 ) >=0 && MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j+1]!=-1){
							RightDiagonal(BlackTeam[Order[n]],round);++StepNumber;return;
						}
					}
					if (BlackTeam[Order[n]].j>1 ) {
						int A_1=MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j-2]*(MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j-2]-1);
			    		int O_1=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j-2]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j-2]+1);
						int O_2=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]+1);
						if ( ( A_1- O_1 - O_2 ) >=0 && MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j-1]!=-1){
							LeftDiagonal(BlackTeam[Order[n]],round);++StepNumber;return;
						}
					}
				}
			}
			for (int n=1;n<6;++n){                            //to protect 3-line attack
				if (MapStatue[5][n]==1 && MapStatue[5][n+1]==1  && MapStatue[6][n]==0 && MapStatue[6][n-1]==0 && MapStatue[6][n]==0 && MapStatue[6][n+1]==0 && MapStatue[6][n+2]==0){
					 for (int nn=0;nn<Teampopulation;++nn){
						 if (BlackTeam[Order[nn]].State==true && BlackTeam[Order[nn]].i==7){							 
							 if (BlackTeam[Order[nn]].j==n-1){
								 StraightAhead(BlackTeam[Order[nn]],round);++StepNumber;return;
							 }
						 }
						 if (BlackTeam[Order[nn]].State==true && BlackTeam[Order[nn]].i==7){							 
							 if (BlackTeam[Order[nn]].j==n+2){
								 StraightAhead(BlackTeam[Order[nn]],round);++StepNumber;return;
							 }
						 }
					 }
				}
				if (n<5){
					if (MapStatue[5][n]==1 && MapStatue[5][n+2]==1  && MapStatue[6][n]==0 && MapStatue[6][n-1]==0 && MapStatue[6][n]==0 && MapStatue[6][n+1]==0 && MapStatue[6][n+2]==0){
						for (int nn=0;nn<Teampopulation;++nn){
							 if ( BlackTeam[Order[nn]].i==7){							 
								 if (BlackTeam[Order[nn]].j==n-1){
									 RightDiagonal(BlackTeam[Order[nn]],round);++StepNumber;return;
								 }
							 }
							 if ( BlackTeam[Order[nn]].i==7){							 
								 if (BlackTeam[Order[nn]].j==n+1){
									 LeftDiagonal(BlackTeam[Order[nn]],round);++StepNumber;return;
								 }
							 }
						 }
					}
				}
			}
			
			for (int n=0;n<Teampopulation;++n){
			      //prefer to attack
					
					if (BlackTeam[Order[n]].j>0 ){
						if (MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j-1]==1){
							LeftDiagonal(BlackTeam[Order[n]],round);++StepNumber;return;
						}						
					}			
					if (BlackTeam[Order[n]].j<7 ){
						if (MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j+1]==1){
							RightDiagonal(BlackTeam[Order[n]],round);++StepNumber;return;
						}					
					}
	
			}
		    
			
			for (int n=0;n<Teampopulation;++n){       //A means ally, O means opponent
						
						if (MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j]==0 ) {           //no obstacle straight ahead	
							if(BlackTeam[Order[n]].j==0  ){
								int A_4=MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j+1]*(MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j+1]-1);
								int O_5=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j+1]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j+1]+1);
								if ( (  A_4 -O_5) >=0 ){
									Straight=true;
								}
							}						
							else if (BlackTeam[Order[n]].j==7 ){
								int A_3=MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j-1]*(MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j-1]-1);
								int O_4=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j-1]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j-1]+1);
								if ( ( A_3 - O_4 ) >=0 ){
									Straight=true;
								}
							}
							else{ 
								int A_4=MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j+1]*(MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j+1]-1);
								int O_5=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j+1]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j+1]+1);
								int A_3=MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j-1]*(MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j-1]-1);
								int O_4=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j-1]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j-1]+1);
								if ( ( A_4 + A_3 - O_4 - O_5 ) >=0 ){
									Straight=true;
								}							
							}
						}	
						if (BlackTeam[Order[n]].j==1 ) {
							int A_2=MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j+2]*(MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j+2]-1);
							int O_2=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]+1);
							int O_3=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j+2]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j+2]+1);
														
							if (MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j-1]!=-1 && MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]!=1){
								Left=true;
							}
							else if ( ( A_2 - O_2 -O_3) >=0 && MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j+1]!=-1){
								Right=true;
							}
						}
						if (BlackTeam[Order[n]].j==6 ) {
				    		int A_1=MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j-2]*(MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j-2]-1);
				    		int O_1=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j-2]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j-2]+1);
							int O_2=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]+1);
							if ( ( A_1 - O_1 - O_2 ) >=0 && MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j-1]!=-1){
								Left=true;
							}
							else if (MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j+1]!=-1 && MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]!=1){
								Right=true;
							}
						}
						if (BlackTeam[Order[n]].j>1 ) {
							int A_1=MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j-2]*(MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j-2]-1);
				    		int O_1=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j-2]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j-2]+1);
							int O_2=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]+1);
							if ( ( A_1- O_1 - O_2 ) >=0 && MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j-1]!=-1){
								Left=true;
							}
						}
						if (BlackTeam[Order[n]].j<6) {
							int A_2=MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j+2]*(MapStatue[BlackTeam[Order[n]].i][BlackTeam[Order[n]].j+2]-1);
							int O_2=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j]+1);
							int O_3=MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j+2]*(MapStatue[BlackTeam[Order[n]].i-2][BlackTeam[Order[n]].j+2]+1);
							if ( ( A_2 - O_2 -O_3 ) >=0 && MapStatue[BlackTeam[Order[n]].i-1][BlackTeam[Order[n]].j+1]!=-1){
								Right=true;
							}
						}
						if (Right){                     //decide direction according to field attack trend
							if (Left){
								if (Straight){
									if (LeftAttackPower<RightAttackPower){
										if (BlackTeam[Order[n]].j>4){
											LeftDiagonal(BlackTeam[Order[n]],round);break;
										}
										else {
											StraightAhead(BlackTeam[Order[n]],round);break;
										}
									}
									else {
										if (BlackTeam[Order[n]].j>4){
											StraightAhead(BlackTeam[Order[n]],round);break;
										}
										else {
											RightDiagonal(BlackTeam[Order[n]],round);break;
										}
									}
								}
								else {
									if (LeftAttackPower<RightAttackPower){
										LeftDiagonal(BlackTeam[Order[n]],round);break;
									}
									else {
										RightDiagonal(BlackTeam[Order[n]],round);break;
									}
								}							
							}
							else if (Straight){
								if (LeftAttackPower<RightAttackPower){
									StraightAhead(BlackTeam[Order[n]],round);break;
								}
								else {
									RightDiagonal(BlackTeam[Order[n]],round);break;
								}
							}
							else {
								RightDiagonal(BlackTeam[Order[n]],round);break;
							}
								
						}
						else if (Left){
							if (Straight){
								if (LeftAttackPower<RightAttackPower){
									LeftDiagonal(BlackTeam[Order[n]],round);break;
								}
								else {
									StraightAhead(BlackTeam[Order[n]],round);break;
								}
							}
							else {
								LeftDiagonal(BlackTeam[Order[n]],round);break;
							}
						}
						else if (Straight){
							StraightAhead(BlackTeam[Order[n]],round);break;
						}
			  								    					
				}

			}					
		++StepNumber;
	}
}

class Operation extends AI{
	
	
	public int CurrentMaxStepNumber;
	public Operation(){
		StepNumber=0;
		Round=1;
		CurrentMaxStepNumber=0;
		for (int j=0;j<8;++j){
			MapStatueRecord[0][0][j]=MapStatueRecord[0][1][j]=1;
			MapStatueRecord[0][6][j]=MapStatueRecord[0][7][j]=-1;	
		}
		for (int i=2;i<6;++i){
			for (int j=0;j<8;++j){
				MapStatueRecord[0][i][j]=0;
			}
		}
	}
	
	public void Undo(int round){
		WhiteBlackWin = 0;
		if (StepNumber>0){
			if (Playmodule) {
				if (WhiteBlackFirst){								
					if ( StepNumber %3 ==2)  {
						StepNumber-=2;
					}
					else {
						if ( StepNumber %3 ==1) StepNumber--;
						else StepNumber-=3;
					}
				}								
				else {
					if ( StepNumber %3 ==0)  {
						StepNumber-=2;
					}
					else {
						if ( StepNumber %3 ==2) StepNumber--;
						else {
							if (StepNumber>1)
								StepNumber-=3;
						}							
					}
				}					
			}
			else {
				StepNumber--;
			}			
			CopyMapStatue(MapStatueRecord[StepNumber],MapStatue);
			
		}
		else 
			CopyMapStatue(MapStatueRecord[StepNumber],MapStatue);
			
		if (BlackMoveList[round]!=0){
			for (int n=0;n<Teampopulation;++n){                 
				if (BlackTeam[Order[n]].i==BlackMoveList[round]/1000 && BlackTeam[Order[n]].j==(BlackMoveList[round]/100)%10 ) {
					BlackTeam[Order[n]].i= (BlackMoveList[round]%100)/10;
					BlackTeam[Order[n]].j= (BlackMoveList[round]%10)%10;
				}
			}
		}
		if ( BlackDeadList[round]!=0 && Playmodule ){
			BlackTeam[Order[Teampopulation]].State=true;
			++Teampopulation;
		}
	}	
}

public class break_through extends Operation {

	private JFrame frmBreakthrough;
    JLabel LabelToMap[][]=new JLabel [8][8];
    
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					break_through window = new break_through();
					window.frmBreakthrough.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public break_through() {
		
		initialize();
	}
    
	
	public void UpdateLabel(int i,int j){
		for (int ii=i-1;ii<=i+1;++ii){
			for (int jj=j-1;jj<=j+1;++jj){
				if (ii>=0 && ii<=7 && jj>=0 && jj<=7)
					StatueToLabel(LabelToMap[ii][jj],MapStatue[ii][jj]);
			}
		}
		if (j<6) StatueToLabel(LabelToMap[i][j+2],MapStatue[i][j+2]);
		if (j>1) StatueToLabel(LabelToMap[i][j-2],MapStatue[i][j-2]);
	}
	
	public void UpdateMap(){                                 //update map according to current mapstatue
		for (int ii=0;ii<8;++ii){
			for (int jj=0;jj<8;++jj){
				StatueToLabel(LabelToMap[ii][jj],MapStatue[ii][jj]);
			}
		}
	}
	
	public void StatueToLabel(JLabel label,int statue){
		switch(statue){
		case 0:
			label.setIcon(null);
			break;
		case 1:
			label.setIcon(new ImageIcon("picture\\white.png"));
			break;
		case 2:
			label.setIcon(new ImageIcon("picture\\white_grey.png"));
			break;
		case 3:
			label.setIcon(new ImageIcon("picture\\Button_Help.png"));
			break;
		case 4:
			label.setIcon(new ImageIcon("picture\\axes.png"));
			break;
		case -1:
			label.setIcon(new ImageIcon("picture\\black.png"));
			break;
		case -2:
			label.setIcon(new ImageIcon("picture\\black_grey.png"));
			break;
		case -3:
			label.setIcon(new ImageIcon("picture\\Button_Help.png"));
			break;
		case -4:
			label.setIcon(new ImageIcon("picture\\axes.png"));
			break;
		}
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmBreakthrough = new JFrame();
		frmBreakthrough.setResizable(false);
		frmBreakthrough.setTitle("Breakthrough");
		frmBreakthrough.setBounds(100, 100, 816, 630);
		frmBreakthrough.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmBreakthrough.getContentPane().setLayout(null);
		
		final JLabel label_A1 = new JLabel("");
		label_A1.setIcon(new ImageIcon("picture\\white.png"));
		label_A1.setBackground(new Color(255, 255, 255));
		label_A1.setBounds(12, 514, 59, 59);
		frmBreakthrough.getContentPane().add(label_A1);
		
		final JLabel label_A2 = new JLabel("");
		label_A2.setIcon(new ImageIcon("picture\\white.png"));
		label_A2.setBackground(Color.WHITE);
		label_A2.setBounds(73, 514, 59, 59);
		frmBreakthrough.getContentPane().add(label_A2);
		
		final JLabel label_A3 = new JLabel("");
		label_A3.setIcon(new ImageIcon("picture\\white.png"));
		label_A3.setBackground(Color.WHITE);
		label_A3.setBounds(144, 514, 59, 59);
		frmBreakthrough.getContentPane().add(label_A3);
		
		final JLabel label_A4 = new JLabel("");
		label_A4.setIcon(new ImageIcon("picture\\white.png"));
		label_A4.setBackground(Color.WHITE);
		label_A4.setBounds(205, 514, 59, 59);
		frmBreakthrough.getContentPane().add(label_A4);
		
		final JLabel label_A5 = new JLabel("");
		label_A5.setIcon(new ImageIcon("picture\\white.png"));
		label_A5.setBackground(Color.WHITE);
		label_A5.setBounds(276, 514, 59, 59);
		frmBreakthrough.getContentPane().add(label_A5);
		
		final JLabel label_A6 = new JLabel("");
		label_A6.setIcon(new ImageIcon("picture\\white.png"));
		label_A6.setBackground(Color.WHITE);
		label_A6.setBounds(337, 514, 59, 59);
		frmBreakthrough.getContentPane().add(label_A6);
		
		final JLabel label_A7 = new JLabel("");
		label_A7.setIcon(new ImageIcon("picture\\white.png"));
		label_A7.setBackground(Color.WHITE);
		label_A7.setBounds(402, 514, 59, 59);
		frmBreakthrough.getContentPane().add(label_A7);
		
		final JLabel label_A8 = new JLabel("");
		label_A8.setIcon(new ImageIcon("picture\\white.png"));
		label_A8.setBackground(Color.WHITE);
		label_A8.setBounds(473, 514, 59, 59);
		frmBreakthrough.getContentPane().add(label_A8);
		
		final JLabel label_B1 = new JLabel("");
		label_B1.setIcon(new ImageIcon("picture\\white.png"));
		label_B1.setBackground(Color.WHITE);
		label_B1.setBounds(12, 442, 59, 59);
		frmBreakthrough.getContentPane().add(label_B1);
		
		final JLabel label_B2 = new JLabel("");
		label_B2.setIcon(new ImageIcon("picture\\white.png"));
		label_B2.setBackground(Color.WHITE);
		label_B2.setBounds(73, 442, 59, 59);
		frmBreakthrough.getContentPane().add(label_B2);
		
		final JLabel label_B3 = new JLabel("");
		label_B3.setIcon(new ImageIcon("picture\\white.png"));
		label_B3.setBackground(Color.WHITE);
		label_B3.setBounds(144, 442, 59, 59);
		frmBreakthrough.getContentPane().add(label_B3);
		
		final JLabel label_B4 = new JLabel("");
		label_B4.setIcon(new ImageIcon("picture\\white.png"));
		label_B4.setBackground(Color.WHITE);
		label_B4.setBounds(205, 442, 59, 59);
		frmBreakthrough.getContentPane().add(label_B4);
		
		final JLabel label_B5 = new JLabel("");
		label_B5.setIcon(new ImageIcon("picture\\white.png"));
		label_B5.setBackground(Color.WHITE);
		label_B5.setBounds(276, 442, 59, 59);
		frmBreakthrough.getContentPane().add(label_B5);
		
		final JLabel label_B6 = new JLabel("");
		label_B6.setIcon(new ImageIcon("picture\\white.png"));
		label_B6.setBackground(Color.WHITE);
		label_B6.setBounds(337, 442, 59, 59);
		frmBreakthrough.getContentPane().add(label_B6);

		final JLabel label_B7 = new JLabel("");
		label_B7.setIcon(new ImageIcon("picture\\white.png"));
		label_B7.setBackground(Color.WHITE);
		label_B7.setBounds(402, 442, 59, 59);
		frmBreakthrough.getContentPane().add(label_B7);
		
		final JLabel label_B8 = new JLabel("");
		label_B8.setIcon(new ImageIcon("picture\\white.png"));
		label_B8.setBackground(Color.WHITE);
		label_B8.setBounds(473, 442, 59, 59);
		frmBreakthrough.getContentPane().add(label_B8);
		
		final JLabel label_C1 = new JLabel("");
		label_C1.setIcon(null);
		label_C1.setBackground(Color.WHITE);
		label_C1.setBounds(12, 370, 59, 59);
		frmBreakthrough.getContentPane().add(label_C1);
		
		final JLabel label_C2 = new JLabel("");
		label_C2.setIcon(null);
		label_C2.setBackground(Color.WHITE);
		label_C2.setBounds(73, 370, 59, 59);
		frmBreakthrough.getContentPane().add(label_C2);
		
		final JLabel label_C3 = new JLabel("");
		label_C3.setIcon(null);
		label_C3.setBackground(Color.WHITE);
		label_C3.setBounds(144, 370, 59, 59);
		frmBreakthrough.getContentPane().add(label_C3);
		
		final JLabel label_C4 = new JLabel("");
		label_C4.setIcon(null);
		label_C4.setBackground(Color.WHITE);
		label_C4.setBounds(205, 370, 59, 59);
		frmBreakthrough.getContentPane().add(label_C4);
		
		final JLabel label_C5 = new JLabel("");
		label_C5.setIcon(null);
		label_C5.setBackground(Color.WHITE);
		label_C5.setBounds(276, 370, 59, 59);
		frmBreakthrough.getContentPane().add(label_C5);
		
		final JLabel label_C6 = new JLabel("");
		label_C6.setIcon(null);
		label_C6.setBackground(Color.WHITE);
		label_C6.setBounds(337, 370, 59, 59);
		frmBreakthrough.getContentPane().add(label_C6);
		
		final JLabel label_C7 = new JLabel("");
		label_C7.setIcon(null);
		label_C7.setBackground(Color.WHITE);
		label_C7.setBounds(402, 370, 59, 59);
		frmBreakthrough.getContentPane().add(label_C7);
		
		final JLabel label_C8 = new JLabel("");
		label_C8.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label_C8.setIcon(null);
		label_C8.setBackground(Color.WHITE);
		label_C8.setBounds(473, 370, 59, 59);
		frmBreakthrough.getContentPane().add(label_C8);
		
		final JLabel label_D1 = new JLabel("");
		label_D1.setIcon(null);
		label_D1.setBackground(Color.WHITE);
		label_D1.setBounds(12, 298, 59, 59);
		frmBreakthrough.getContentPane().add(label_D1);
		
		final JLabel label_D2 = new JLabel("");
		label_D2.setIcon(null);
		label_D2.setBackground(Color.WHITE);
		label_D2.setBounds(73, 298, 59, 59);
		frmBreakthrough.getContentPane().add(label_D2);
		
		final JLabel label_D3 = new JLabel("");
		label_D3.setIcon(null);
		label_D3.setBackground(Color.WHITE);
		label_D3.setBounds(144, 298, 59, 59);
		frmBreakthrough.getContentPane().add(label_D3);
		
		final JLabel label_D4 = new JLabel("");
		label_D4.setIcon(null);
		label_D4.setBackground(Color.WHITE);
		label_D4.setBounds(205, 298, 59, 59);
		frmBreakthrough.getContentPane().add(label_D4);
		
		final JLabel label_D5 = new JLabel("");
		label_D5.setIcon(null);
		label_D5.setBackground(Color.WHITE);
		label_D5.setBounds(276, 298, 59, 59);
		frmBreakthrough.getContentPane().add(label_D5);
		
		final JLabel label_D6 = new JLabel("");
		label_D6.setIcon(null);
		label_D6.setBackground(Color.WHITE);
		label_D6.setBounds(337, 298, 59, 59);
		frmBreakthrough.getContentPane().add(label_D6);
		
		final JLabel label_D7 = new JLabel("");
		label_D7.setIcon(null);
		label_D7.setBackground(Color.WHITE);
		label_D7.setBounds(402, 298, 59, 59);
		frmBreakthrough.getContentPane().add(label_D7);
		
		final JLabel label_D8 = new JLabel("");
		label_D8.setIcon(null);
		label_D8.setBackground(Color.WHITE);
		label_D8.setBounds(473, 298, 59, 59);
		frmBreakthrough.getContentPane().add(label_D8);
		
		final JLabel label_E1 = new JLabel("");
		label_E1.setIcon(null);
		label_E1.setBackground(Color.WHITE);
		label_E1.setBounds(12, 226, 59, 59);
		frmBreakthrough.getContentPane().add(label_E1);
		
		final JLabel label_E2 = new JLabel("");
		label_E2.setIcon(null);
		label_E2.setBackground(Color.WHITE);
		label_E2.setBounds(73, 226, 59, 59);
		frmBreakthrough.getContentPane().add(label_E2);
		
		final JLabel label_E3 = new JLabel("");
		label_E3.setIcon(null);
		label_E3.setBackground(Color.WHITE);
		label_E3.setBounds(144, 226, 59, 59);
		frmBreakthrough.getContentPane().add(label_E3);
		
		final JLabel label_E4 = new JLabel("");
		label_E4.setIcon(null);
		label_E4.setBackground(Color.WHITE);
		label_E4.setBounds(205, 226, 59, 59);
		frmBreakthrough.getContentPane().add(label_E4);

		final JLabel label_E5 = new JLabel("");
		label_E5.setIcon(null);
		label_E5.setBackground(Color.WHITE);
		label_E5.setBounds(276, 226, 59, 59);
		frmBreakthrough.getContentPane().add(label_E5);
		
		final JLabel label_E6 = new JLabel("");
		label_E6.setIcon(null);
		label_E6.setBackground(Color.WHITE);
		label_E6.setBounds(337, 226, 59, 59);
		frmBreakthrough.getContentPane().add(label_E6);
		
		final JLabel label_E7 = new JLabel("");
		label_E7.setIcon(null);
		label_E7.setBackground(Color.WHITE);
		label_E7.setBounds(402, 226, 59, 59);
		frmBreakthrough.getContentPane().add(label_E7);
		
		final JLabel label_E8 = new JLabel("");
		label_E8.setIcon(null);
		label_E8.setBackground(Color.WHITE);
		label_E8.setBounds(473, 226, 59, 59);
		frmBreakthrough.getContentPane().add(label_E8);
		
		final JLabel label_F1 = new JLabel("");
		label_F1.setIcon(null);
		label_F1.setBackground(Color.WHITE);
		label_F1.setBounds(12, 154, 59, 59);
		frmBreakthrough.getContentPane().add(label_F1);
		
		final JLabel label_F2 = new JLabel("");
		label_F2.setIcon(null);
		label_F2.setBackground(Color.WHITE);
		label_F2.setBounds(73, 154, 59, 59);
		frmBreakthrough.getContentPane().add(label_F2);
		
		final JLabel label_F3 = new JLabel("");
		label_F3.setIcon(null);
		label_F3.setBackground(Color.WHITE);
		label_F3.setBounds(144, 154, 59, 59);
		frmBreakthrough.getContentPane().add(label_F3);
		
		final JLabel label_F4 = new JLabel("");
		label_F4.setIcon(null);
		label_F4.setBackground(Color.WHITE);
		label_F4.setBounds(205, 154, 59, 59);
		frmBreakthrough.getContentPane().add(label_F4);
		
		final JLabel label_F5 = new JLabel("");
		label_F5.setIcon(null);
		label_F5.setBackground(Color.WHITE);
		label_F5.setBounds(276, 154, 59, 59);
		frmBreakthrough.getContentPane().add(label_F5);
		
		final JLabel label_F6 = new JLabel("");
		label_F6.setIcon(null);
		label_F6.setBackground(Color.WHITE);
		label_F6.setBounds(337, 154, 59, 59);
		frmBreakthrough.getContentPane().add(label_F6);
		
		final JLabel label_F7 = new JLabel("");
		label_F7.setIcon(null);
		label_F7.setBackground(Color.WHITE);
		label_F7.setBounds(402, 154, 59, 59);
		frmBreakthrough.getContentPane().add(label_F7);
		
		final JLabel label_F8 = new JLabel("");
		label_F8.setIcon(null);
		label_F8.setBackground(Color.WHITE);
		label_F8.setBounds(473, 154, 59, 59);
		frmBreakthrough.getContentPane().add(label_F8);
		
		final JLabel label_G1 = new JLabel("");
		label_G1.setIcon(new ImageIcon("picture\\black.png"));
		label_G1.setBackground(Color.WHITE);
		label_G1.setBounds(12, 85, 59, 59);
		frmBreakthrough.getContentPane().add(label_G1);
		
		final JLabel label_G2 = new JLabel("");
		label_G2.setIcon(new ImageIcon("picture\\black.png"));
		label_G2.setBackground(Color.WHITE);
		label_G2.setBounds(73, 85, 59, 59);
		frmBreakthrough.getContentPane().add(label_G2);
		
		final JLabel label_G3 = new JLabel("");
		label_G3.setIcon(new ImageIcon("picture\\black.png"));
		label_G3.setBackground(Color.WHITE);
		label_G3.setBounds(144, 85, 59, 59);
		frmBreakthrough.getContentPane().add(label_G3);
		
		final JLabel label_G4 = new JLabel("");
		label_G4.setIcon(new ImageIcon("picture\\black.png"));
		label_G4.setBackground(Color.WHITE);
		label_G4.setBounds(205, 85, 59, 59);
		frmBreakthrough.getContentPane().add(label_G4);
		
		final JLabel label_G5 = new JLabel("");
		label_G5.setIcon(new ImageIcon("picture\\black.png"));
		label_G5.setBackground(Color.WHITE);
		label_G5.setBounds(276, 85, 59, 59);
		frmBreakthrough.getContentPane().add(label_G5);
		
		final JLabel label_G6 = new JLabel("");
		label_G6.setIcon(new ImageIcon("picture\\black.png"));
		label_G6.setBackground(Color.WHITE);
		label_G6.setBounds(337, 85, 59, 59);
		frmBreakthrough.getContentPane().add(label_G6);
		
		final JLabel label_G7 = new JLabel("");
		label_G7.setIcon(new ImageIcon("picture\\black.png"));
		label_G7.setBackground(Color.WHITE);
		label_G7.setBounds(402, 85, 59, 59);
		frmBreakthrough.getContentPane().add(label_G7);
		
		final JLabel label_G8 = new JLabel("");
		label_G8.setIcon(new ImageIcon("picture\\black.png"));
		label_G8.setBackground(Color.WHITE);
		label_G8.setBounds(473, 85, 59, 59);
		frmBreakthrough.getContentPane().add(label_G8);
		
		final JLabel label_H1 = new JLabel("");
		label_H1.setIcon(new ImageIcon("picture\\black.png"));
		label_H1.setBackground(Color.WHITE);
		label_H1.setBounds(12, 13, 59, 59);
		frmBreakthrough.getContentPane().add(label_H1);
		
		final JLabel label_H2 = new JLabel("");
		label_H2.setIcon(new ImageIcon("picture\\black.png"));
		label_H2.setBackground(Color.WHITE);
		label_H2.setBounds(73, 13, 59, 59);
		frmBreakthrough.getContentPane().add(label_H2);
		
		final JLabel label_H3 = new JLabel("");
		label_H3.setIcon(new ImageIcon("picture\\black.png"));
		label_H3.setBackground(Color.WHITE);
		label_H3.setBounds(144, 13, 59, 59);
		frmBreakthrough.getContentPane().add(label_H3);
		
		final JLabel label_H4 = new JLabel("");
		label_H4.setIcon(new ImageIcon("picture\\black.png"));
		label_H4.setBackground(Color.WHITE);
		label_H4.setBounds(205, 13, 59, 59);
		frmBreakthrough.getContentPane().add(label_H4);
		
		final JLabel label_H5 = new JLabel("");
		label_H5.setIcon(new ImageIcon("picture\\black.png"));
		label_H5.setBackground(Color.WHITE);
		label_H5.setBounds(276, 13, 59, 59);
		frmBreakthrough.getContentPane().add(label_H5);
		
		final JLabel label_H6 = new JLabel("");
		label_H6.setIcon(new ImageIcon("picture\\black.png"));
		label_H6.setBackground(Color.WHITE);
		label_H6.setBounds(337, 13, 59, 59);
		frmBreakthrough.getContentPane().add(label_H6);
		
		final JLabel label_H7 = new JLabel("");
		label_H7.setIcon(new ImageIcon("picture\\black.png"));
		label_H7.setBackground(Color.WHITE);
		label_H7.setBounds(402, 13, 59, 59);
		frmBreakthrough.getContentPane().add(label_H7);
		
		final JLabel label_H8 = new JLabel("");
		label_H8.setIcon(new ImageIcon("picture\\black.png"));
		label_H8.setBackground(Color.WHITE);
		label_H8.setBounds(473, 13, 59, 59);
		frmBreakthrough.getContentPane().add(label_H8);
		
		final JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("picture\\qipan.PNG"));
		lblNewLabel.setBounds(0, 13, 539, 560);
		frmBreakthrough.getContentPane().add(lblNewLabel);
		
		final JLabel lblShowStepNumber = new JLabel("StepNumber: 0");
		lblShowStepNumber.setBounds(630, 269, 117, 39);
		frmBreakthrough.getContentPane().add(lblShowStepNumber);
		
		final JLabel lblShowRound = new JLabel("Round: 0");
		lblShowRound.setBounds(630, 298, 117, 39);
		frmBreakthrough.getContentPane().add(lblShowRound);
		
		final JLabel lblShowResult = new JLabel(" ");
		lblShowResult.setBounds(630, 427, 117, 39);
		frmBreakthrough.getContentPane().add(lblShowResult);
		
		JButton btnUndo = new JButton("Undo");
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (WhiteBlackWin != 0){
					for (int ii=0;ii<8;++ii){
						for(int jj=0;jj<8;++jj){
							LabelToMap[ii][jj].setEnabled(true);
						}
					}
				}
				Undo(Round);
				UpdateMap();				
				if (Playmodule) {
					Round=(StepNumber-1)/3+1;
					if (WhiteBlackFirst){								
						if ( StepNumber %3 ==2)  {
							WhiteBlackTurn= false;
						}
						else {
							WhiteBlackTurn= true;
							if ( StepNumber %3 ==1) Hold= true;
							else Hold= false;
						}
					}								
					else {
						if ( StepNumber %3 ==0)  {
							WhiteBlackTurn= false;
						}
						else {
							WhiteBlackTurn= true;
							if ( StepNumber %3 ==2) Hold= true;
							else Hold= false;
						}
					}					
				}
				else {
					Round=(StepNumber-1)/4+1;
					if ( (StepNumber/2) %2 ==0)  WhiteBlackTurn= true;
					else WhiteBlackTurn= false;
					if ( StepNumber %2 ==0) Hold= true;
					else Hold= false;
				}
				lblShowStepNumber.setText("StepNumber: "+String.valueOf(StepNumber));
				lblShowRound.setText("Round: "+String.valueOf(Round));
			}
		});
		btnUndo.setBounds(630, 85, 97, 25);
		frmBreakthrough.getContentPane().add(btnUndo);
		
		JButton btnRestart = new JButton("Restart");
		btnRestart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Restart();
				UpdateMap();
				lblShowStepNumber.setText("StepNumber: "+String.valueOf(StepNumber));
				lblShowRound.setText("Round: "+String.valueOf(Round));
				lblShowResult.setText(" ");
				for (int ii=0;ii<8;++ii){
					for(int jj=0;jj<8;++jj){
						LabelToMap[ii][jj].setEnabled(true);
					}
				}
			}
		});
		btnRestart.setBounds(630, 165, 97, 25);
		frmBreakthrough.getContentPane().add(btnRestart);
		
		LabelToMap[0][0]=label_A1;
		LabelToMap[0][1]=label_A2;
		LabelToMap[0][2]=label_A3;
		LabelToMap[0][3]=label_A4;
		LabelToMap[0][4]=label_A5;
		LabelToMap[0][5]=label_A6;
		LabelToMap[0][6]=label_A7;
		LabelToMap[0][7]=label_A8;
		LabelToMap[1][0]=label_B1;
		LabelToMap[1][1]=label_B2;
		LabelToMap[1][2]=label_B3;
		LabelToMap[1][3]=label_B4;
		LabelToMap[1][4]=label_B5;
		LabelToMap[1][5]=label_B6;
		LabelToMap[1][6]=label_B7;
		LabelToMap[1][7]=label_B8;
		LabelToMap[2][0]=label_C1;
		LabelToMap[2][1]=label_C2;
		LabelToMap[2][2]=label_C3;
		LabelToMap[2][3]=label_C4;
		LabelToMap[2][4]=label_C5;
		LabelToMap[2][5]=label_C6;
		LabelToMap[2][6]=label_C7;
		LabelToMap[2][7]=label_C8;
		LabelToMap[3][0]=label_D1;
		LabelToMap[3][1]=label_D2;
		LabelToMap[3][2]=label_D3;
		LabelToMap[3][3]=label_D4;
		LabelToMap[3][4]=label_D5;
		LabelToMap[3][5]=label_D6;
		LabelToMap[3][6]=label_D7;
		LabelToMap[3][7]=label_D8;
		LabelToMap[4][0]=label_E1;
		LabelToMap[4][1]=label_E2;
		LabelToMap[4][2]=label_E3;
		LabelToMap[4][3]=label_E4;
		LabelToMap[4][4]=label_E5;
		LabelToMap[4][5]=label_E6;
		LabelToMap[4][6]=label_E7;
		LabelToMap[4][7]=label_E8;
		LabelToMap[5][0]=label_F1;
		LabelToMap[5][1]=label_F2;
		LabelToMap[5][2]=label_F3;
		LabelToMap[5][3]=label_F4;
		LabelToMap[5][4]=label_F5;
		LabelToMap[5][5]=label_F6;
		LabelToMap[5][6]=label_F7;
		LabelToMap[5][7]=label_F8;
		LabelToMap[6][0]=label_G1;
		LabelToMap[6][1]=label_G2;
		LabelToMap[6][2]=label_G3;
		LabelToMap[6][3]=label_G4;
		LabelToMap[6][4]=label_G5;
		LabelToMap[6][5]=label_G6;
		LabelToMap[6][6]=label_G7;
		LabelToMap[6][7]=label_G8;
		LabelToMap[7][0]=label_H1;
		LabelToMap[7][1]=label_H2;
		LabelToMap[7][2]=label_H3;
		LabelToMap[7][3]=label_H4;
		LabelToMap[7][4]=label_H5;
		LabelToMap[7][5]=label_H6;
		LabelToMap[7][6]=label_H7;
		LabelToMap[7][7]=label_H8;
		
		
		
		final JRadioButton rdbtnPlayerVSAI = new JRadioButton("Player vs AI",true);
		rdbtnPlayerVSAI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Playmodule=true;
			}
		});
		rdbtnPlayerVSAI.setBounds(620, 13, 127, 25);
		frmBreakthrough.getContentPane().add(rdbtnPlayerVSAI);
		
		final JRadioButton rdbtnPlayerVsPlayer = new JRadioButton("Player vs Player");
		rdbtnPlayerVsPlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Playmodule=false;
			}
		});
		rdbtnPlayerVsPlayer.setBounds(620, 43, 127, 25);
		frmBreakthrough.getContentPane().add(rdbtnPlayerVsPlayer);
		
		ButtonGroup SetPlaymodule=new ButtonGroup();
		SetPlaymodule.add(rdbtnPlayerVSAI);
		SetPlaymodule.add(rdbtnPlayerVsPlayer);
		
		final JRadioButton rdbtnWhiteFirst = new JRadioButton("White First",true);
		rdbtnWhiteFirst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WhiteBlackFirst=true;
				WhiteBlackTurn=true;
			}
		});
		rdbtnWhiteFirst.setBounds(577, 199, 97, 25);
		frmBreakthrough.getContentPane().add(rdbtnWhiteFirst);
		
		final JRadioButton rdbtnBlackFirst = new JRadioButton("Black First");
		rdbtnBlackFirst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				WhiteBlackFirst=false;
				WhiteBlackTurn=false;
				AnalysisBlack(StepNumber);
				UpdateMap();
				Round=(StepNumber-1)/3+1;
				lblShowStepNumber.setText("StepNumber: "+String.valueOf(StepNumber));	
				lblShowRound.setText("Round: "+String.valueOf(Round));
				WhiteBlackTurn= true;
				if (StepNumber>CurrentMaxStepNumber)
				CurrentMaxStepNumber++;
				CopyMapStatue(MapStatue,MapStatueRecord[StepNumber]);
			}
		});
		rdbtnBlackFirst.setBounds(694, 199, 97, 25);
		frmBreakthrough.getContentPane().add(rdbtnBlackFirst);
		
		ButtonGroup SetFirst=new ButtonGroup();
		SetFirst.add(rdbtnWhiteFirst);
		SetFirst.add(rdbtnBlackFirst);
		
		for (int i=0;i<8;++i){
			for(int j=0;j<8;++j){
				final int iii=i;
				final int jjj=j;
				LabelToMap[i][j].addMouseListener(new MouseAdapter() {
					@Override
					
					public void mouseClicked(MouseEvent arg0) {
						StepNumber++;
						Manipulation(iii,jjj,StepNumber);
						UpdateLabel(iii,jjj);		
						if (StepNumber>CurrentMaxStepNumber)
							CurrentMaxStepNumber++;
						CopyMapStatue(MapStatue,MapStatueRecord[StepNumber]);							
						if (Playmodule) {
							Round=(StepNumber-1)/3+1;
							if (WhiteBlackFirst){								
								if ( StepNumber %3 ==2)  WhiteBlackTurn= false;
								else WhiteBlackTurn= true;
							}								
							else {
								if ( StepNumber %3 ==0)  WhiteBlackTurn= false;
								else WhiteBlackTurn= true;
							}
						}
						else {
							Round=(StepNumber-1)/4+1;
							if ( (StepNumber/2) %2 ==0)  WhiteBlackTurn= true;
							else WhiteBlackTurn= false;
						}
						lblShowStepNumber.setText("StepNumber: "+String.valueOf(StepNumber));
						lblShowRound.setText("Round: "+String.valueOf(Round));
						DetectWhoWin();
						if (WhiteBlackWin == 1){
							lblShowResult.setText(" White Win !! ");
							for (int ii=0;ii<8;++ii){
								for(int jj=0;jj<8;++jj){
									LabelToMap[ii][jj].setEnabled(false);
								}
							}
						}
						else if (WhiteBlackWin == -1){
							lblShowResult.setText(" Black Win !! ");
							for (int ii=0;ii<8;++ii){
								for(int jj=0;jj<8;++jj){
									LabelToMap[ii][jj].setEnabled(false);
								}
							}
						}
						
						if (Playmodule && !WhiteBlackTurn) {
							AnalysisBlack(StepNumber);
							UpdateMap();
							Round=(StepNumber-1)/3+1;
							lblShowStepNumber.setText("StepNumber: "+String.valueOf(StepNumber));	
							lblShowRound.setText("Round: "+String.valueOf(Round));
							WhiteBlackTurn= true;
							if (StepNumber>CurrentMaxStepNumber)
							CurrentMaxStepNumber++;
							CopyMapStatue(MapStatue,MapStatueRecord[StepNumber]);
							DetectWhoWin();
							if (WhiteBlackWin == 1){
								lblShowResult.setText(" White Win !! ");
								for (int ii=0;ii<8;++ii){
									for(int jj=0;jj<8;++jj){
										LabelToMap[ii][jj].setEnabled(false);
									}
								}
							}
							else if (WhiteBlackWin == -1){
								lblShowResult.setText(" Black Win !! ");
								for (int ii=0;ii<8;++ii){
									for(int jj=0;jj<8;++jj){
										LabelToMap[ii][jj].setEnabled(false);
									}
								}
							}
						}
						
					}
				});
				LabelToMap[i][j].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			
		}
	}
	
	
}
