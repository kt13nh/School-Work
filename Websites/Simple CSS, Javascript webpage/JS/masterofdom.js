/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function h3ToP(){
	/*declare count to count number of times to loop 
	function, where count is the length of h3 tags*/
    var count;
    count = document.getElementsByTagName('h3').length;
	console.log(count);
    for(var i = 0;i<count;i++){
		/*declare tagName, where tagName is the h3 tag at 
		index 0, and create new Element as newTag, where newTag
		is the text of tagName. tagName is then replaced with 
		newTag
		*/
        var tagName;
        var newTag;
        tagName = document.getElementsByTagName('h3')[0];
        newTag = document.createElement('p');
        newTag.innerHTML = tagName.innerHTML;
        tagName.parentNode.replaceChild(newTag, tagName);
    }
}

function olToUl(){
	/*declare count to count number of times to loop 
	function, where count is the length of ol tags*/
    var count;
    count = document.getElementsByTagName('ol').length;
    for(var i=0;i<count;i++){
		/*declare tagName, where tagName is the ol tag at 
		index 0, and create new Element as newTag, where newTag
		is the text of tagName. tagName is then replaced with 
		newTag
		*/
        var tagName;
        var newTag;
        tagName = document.getElementsByTagName('ol')[0];
        newTag = document.createElement('ul');
        newTag.innerHTML = tagName.innerHTML;
        tagName.parentNode.replaceChild(newTag, tagName);
    }
}
function strongToEm(){
	/*declare count to count number of times to loop 
	function, where count is the length of strong tags*/
    var count;
    count = document.getElementsByTagName('strong').length;
    for(var i=0;i<count;i++){
		/*declare tagName, where tagName is the strong tag at 
		index 0, and create new Element as newTag, where newTag
		is the text of tagName. tagName is then replaced with 
		newTag
		*/
        var tagName;
        var newTag;
        tagName = document.getElementsByTagName('strong')[0];
        newTag = document.createElement('em');
        newTag.innerHTML = tagName.innerHTML;
        tagName.parentNode.replaceChild(newTag, tagName);
    }
}
function removeH6(){
	/*declare count to count number of times to loop 
	function, where count is the length of h6 tags*/
    var count; 
    count = document.getElementsByTagName('h6').length;
    for(var i=0;i<count;i++){
		/*declare tagName, where tagName is the h6 tag at 
		index 0. Remove tagName.
		*/
        var tagName;
        tagName = document.getElementsByTagName('h6')[0];
        tagName.parentNode.removeChild(tagName);
    }
}
