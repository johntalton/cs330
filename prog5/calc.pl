%# John Talton
%# CS330
%# Blah Blah some prolog stuff that is
%#  Realy realy fun. :)

%All the butiful calculus rules 
%  for taking derivitivs
calc(X,X,1).                       
calc(C,_,0) :- integer(C).         
calc(Z+Y,X,A+B) :- calc(Z,X,A), calc(Y,X,B).
calc(Z-Y,X,A-B) :- calc(Z,X,A), calc(Y,X,B).
calc(Y,X,0) :- atomic(Y),  X \= Y. 
calc(U*V,X,U*Dv+V*Du) :- calc(U,X,Du),calc(V,X,Dv).
calc(U/V,X,(V*Du-U*Dv)/(V^2)):-calc(U,X,Du),calc(V,X,Dv).
calc(U^N,X,N*(U^(N-1))*Du) :- integer(N),calc(U,X,Du).
calc(log(U),X,Du / U) :- calc(U,X,B),redu(B,Du).

%Some prity reduction rules.. fun
redu(-A,-B):- redu(A,B).
redu(A^1,A):- atomic(A).
redu(A^1,B):- redu(A,B). 
redu(_^0,1).
redu(A^B,C^D) :- redu(A,C),redu(B,D).
redu(U^V, Ru^Rv):- redu(U,Ru), redu(V,Rv).
redu(U-0,R) :- redu(U,R).
redu(A-B,C):-integer(A),integer(B),C is A-B. 
redu(U-V, Su-Sv):- redu(U,Su), redu(V,Sv).
redu(A+(-A),0).
redu((-A)+A,0).
redu(U+0,R) :- redu(U,R).
redu(0+U,R) :- U\=0,redu(U,R).
redu(A+B,C):-integer(A),integer(B),C is A+B. 
redu(U+V, Su+Sv):- redu(U,Su), redu(V,Sv).
redu(_*0,0).
redu(0*_,0).
redu(1*U,R) :- redu(U,R).
redu(U*1,R) :- redu(U,R).
redu(U*V, Su*Sv):- redu(U,Su), redu(V,Sv).
redu(U/V, Su/Sv):- redu(U,Su), redu(V,Sv).
redu(X,X).

%Here is a small driver to use dx as defult
driver(Y,X,D) :- calc(Y,X,Z),redu(Z,C),redu(C,D).

%The main program call
der(Y,R) :- driver(Y,x,R).
