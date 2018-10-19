CREATE TABLE IF NOT EXISTS Posts (
  Id INT NOT NULL,
  PostTypeId TINYINT NOT NULL,
  ParentId INT,
  AcceptedAnswerId INT,
  OwnerUserId INT,
  LastEditorUserId INT,
  OwnerDisplayName VARCHAR(50),
  LastEditorDisplayName VARCHAR(50),
  Title VARCHAR(250),
  Tags VARCHAR(250),
  Body TEXT,
  Score INT,
  ViewCount INT,
  FavoriteCount INT,
  AnswerCount INT,
  CommentCount INT,
  CreationDate TIMESTAMP,
  CommunityOwnedDate TIMESTAMP,
  LastEditDate TIMESTAMP,
  ClosedDate TIMESTAMP,
  LastActivityDate TIMESTAMP,
  PRIMARY KEY(Id)
) ENGINE=MyISAM;

alter table Posts add FULLTEXT `tags_ft_idx` (tags);
alter table Posts add index `posttypeid_idx` (PostTypeId);

CREATE TABLE IF NOT EXISTS Badges (
  Id INT NOT NULL,
  PRIMARY KEY(Id)
) ENGINE=MyISAM;