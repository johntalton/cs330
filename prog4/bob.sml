(*John Talton*)
(*CS300*)
let
   fun findME([]) = false
   |   findME("me"::_) = true
   |   findME(_::blah) = findME(blah)

   fun outList([]) =  (output (std_out, ""))
   |   outList(something::[]) = (output (std_out,something))
   |   outList(something::blah) = 
       let in
          (output (std_out,something));
          (output (std_out, " "));
          outList(blah)
       end

   fun talk([]) = (output (std_out, "In what way?\n"))
   |  talk("my"::single::therest) =
      if (findME(therest)) then 
      let
      in
         (output (std_out, "Tell me about your "));
         (output (std_out, single));
         (output (std_out, "\n"))         
      end 
      else
      let in
         (output (std_out, "In what way?\n"));
         (output (std_out, "\n"))     
      end
   |  talk("i"::"am"::something) =
      let in
         (output (std_out, "I am sorry to hear that you are "));
         outList(something);
         (output (std_out, "\n"))
      end
   |  talk("am"::"i"::junk) = 
      let in
         (output (std_out, "Do you believe that you are "));
         outList(junk);
         (output (std_out, "?\n")) 
      end  
   |  talk("you"::blah::stuff) = 
      if (findME(stuff)) then
      let in
         (output (std_out, "Why do you thing I "));
         (output (std_out, blah));
         (output (std_out, " you?"));
         (output (std_out, "\n"))       
      end   
      else (output (std_out, "In what way?\n"))       
   |  talk(_::otherstuff) = talk(otherstuff);
   
          
   fun GetWord(start,length,String) =
      if(start+length >= size (String)) then
         substring(String,start,length)
      else if( substring(String,start+length,1) = " ") then 
         substring(String,start,length)
      else 
         GetWord(start,length + 1,String);
   
   
   fun Split(String,Start) =
      if (Start >= size(String)) then 
         [] 
      else
         GetWord(Start,0,String)::Split(String,Start + size(GetWord(Start,0,String)) + 1);
    
   val file_in = open_in("talk.dat");
   fun ReadInput() = 
      let 
          val sentence = input_line(file_in);
            
      in
         if(size (sentence) <> 0) then
         let
            val sent = substring(sentence,0,size(sentence) - 1)
         in
            talk(Split(sent,0));
            ReadInput()
         end
         else
            (output (std_out, "\n\nNice talking with you.\n\n"))
      end;
   

in
   (output (std_out, "--==Welcome to BOB, the semi-intelectual==--\n\n"));
   ReadInput()

end;
