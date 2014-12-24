package org.exoplatform.brandadvocacy.model;

/**
 * Created by exoplatform on 24/12/14.
 */
public enum  NoteType {
  AdminComment(1),
  RejectParcipantReason(2);

  private final int type;
  NoteType(int type){
    this.type = type;
  }
  public int getType(){
    return this.type;
  }
  public String getValue(){
    switch (this.type){
      case (1):
        return "admim comment";
      case (2):
        return "reject mission participant reason";
      default:
        return "admin comment";
    }
  }
  public static NoteType getNoteType(int type){
    for (NoteType noteType: NoteType.values()){
      if (noteType.getType() == type){
        return noteType;
      }
    }
    return AdminComment;
  }
}
